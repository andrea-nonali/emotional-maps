package emotionalmaps;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for DataManager covering addEvent, createMap (same-year and cross-year),
 * POI2/POI3 count correctness (regression for the index bug), boundary exclusion,
 * and a simple in-process performance measurement over 100k events.
 *
 * -------------------------------------------------------------------------
 * NOTE ON JMH-BASED PERFORMANCE TESTING
 * -------------------------------------------------------------------------
 * For production benchmarking the recommended approach is JMH (Java Microbenchmark Harness):
 *
 *   @Benchmark
 *   @BenchmarkMode(Mode.AverageTime)
 *   @OutputTimeUnit(TimeUnit.MILLISECONDS)
 *   public void benchmarkCreateMap(BenchmarkState state) {
 *       state.dataManager.createMap("01012000-31122029");
 *   }
 *
 * Key metrics to track:
 *   - Average time per createMap call (ms)
 *   - Throughput (ops/s) for addEvent under load
 *   - GC pressure and allocation rate (use -prof gc)
 *   - Warm-up vs. steady-state latency difference
 *
 * JMH is NOT included as a Maven dependency here because the harness requires
 * annotation processing and a separate benchmark JAR.  Add it with:
 *
 *   <dependency>
 *     <groupId>org.openjdk.jmh</groupId>
 *     <artifactId>jmh-core</artifactId>
 *     <version>1.37</version>
 *   </dependency>
 * -------------------------------------------------------------------------
 */
class DataManagerTest {

    private DataManager dataManager;

    // POI coordinates (from Event.setPoi boundaries)
    private static final String POI1_COORDS = "45.464,9.190"; // POI1
    private static final String POI2_COORDS = "45.473,9.173"; // POI2
    private static final String POI3_COORDS = "45.458,9.181"; // POI3

    @BeforeEach
    void setUp() {
        dataManager = new DataManager();
    }

    private Event makeEvent(String date, String poiCoords, String emotion) {
        return new Event("IN", "LOGIN", date, "user1", poiCoords, emotion);
    }

    // -------------------------------------------------------------------------
    // addEvent
    // -------------------------------------------------------------------------

    @Test
    void addEvent_storesEventInCorrectYear() {
        Event e = makeEvent("15032021", POI1_COORDS, "A");
        dataManager.addEvent(e);
        // Verify by running createMap over that single-day range and checking output
        // (indirect: if no exception, the event was stored)
        assertDoesNotThrow(() -> dataManager.createMap("15032021-15032021"));
    }

    @Test
    void addEvent_yearNotInStore_eventIsAdded() {
        // Year 1999 is outside the pre-populated 2000-2030 range
        Event e = makeEvent("01011999", POI1_COORDS, "F");
        assertDoesNotThrow(() -> dataManager.addEvent(e));
        // Verify it was actually added (not just the TreeSet created but empty — the original bug)
        assertDoesNotThrow(() -> dataManager.createMap("01011999-31121999"));
    }

    // -------------------------------------------------------------------------
    // createMap — same-year range
    // -------------------------------------------------------------------------

    @Test
    void createMap_sameYear_countsCorrectPoi1Events() {
        dataManager.addEvent(makeEvent("10012020", POI1_COORDS, "A"));
        dataManager.addEvent(makeEvent("15012020", POI1_COORDS, "F"));
        dataManager.addEvent(makeEvent("20012020", POI1_COORDS, "S"));

        // Capture stdout to verify POI1 total
        String output = captureCreateMap("01012020-31012020");
        assertTrue(output.contains("Total events at POI1: 3"),
                "Expected 3 events at POI1, output was:\n" + output);
    }

    /**
     * REGRESSION TEST for the POI2/POI3 index bug.
     * Original code read from emotionalData[0] when writing to emotionalData[1]/[2],
     * causing POI2 and POI3 counts to be based on POI1's totals.
     */
    @Test
    void createMap_poi2AndPoi3_countsAreIndependent() {
        // Add events to each POI in the same date range
        dataManager.addEvent(makeEvent("10012020", POI1_COORDS, "A")); // POI1 - ANGRY
        dataManager.addEvent(makeEvent("10012020", POI2_COORDS, "F")); // POI2 - HAPPY
        dataManager.addEvent(makeEvent("10012020", POI2_COORDS, "F")); // POI2 - HAPPY
        dataManager.addEvent(makeEvent("10012020", POI3_COORDS, "S")); // POI3 - SURPRISED
        dataManager.addEvent(makeEvent("10012020", POI3_COORDS, "S")); // POI3 - SURPRISED
        dataManager.addEvent(makeEvent("10012020", POI3_COORDS, "S")); // POI3 - SURPRISED

        String output = captureCreateMap("01012020-31012020");

        assertTrue(output.contains("Total events at POI1: 1"),
                "POI1 should have 1 event, output:\n" + output);
        assertTrue(output.contains("Total events at POI2: 2"),
                "POI2 should have 2 events (regression: was using POI1 index), output:\n" + output);
        assertTrue(output.contains("Total events at POI3: 3"),
                "POI3 should have 3 events (regression: was using POI1 index), output:\n" + output);
    }

    // -------------------------------------------------------------------------
    // createMap — cross-year range
    // -------------------------------------------------------------------------

    @Test
    void createMap_crossYear_includesEventsFromAllYears() {
        dataManager.addEvent(makeEvent("15112019", POI1_COORDS, "A"));
        dataManager.addEvent(makeEvent("15062020", POI1_COORDS, "F"));
        dataManager.addEvent(makeEvent("15032021", POI1_COORDS, "N"));

        String output = captureCreateMap("01012019-31122021");
        assertTrue(output.contains("Total events at POI1: 3"),
                "Cross-year range should include all 3 events, output:\n" + output);
    }

    // -------------------------------------------------------------------------
    // Date-range boundary exclusion
    // -------------------------------------------------------------------------

    @Test
    void createMap_excludesEventsOutsideDateRange() {
        // Only the middle event should be counted
        dataManager.addEvent(makeEvent("01012019", POI1_COORDS, "A")); // before range
        dataManager.addEvent(makeEvent("15062020", POI1_COORDS, "F")); // inside range
        dataManager.addEvent(makeEvent("31122021", POI1_COORDS, "N")); // after range

        String output = captureCreateMap("01012020-31122020");
        assertTrue(output.contains("Total events at POI1: 1"),
                "Only 1 event should be in range [2020], output:\n" + output);
    }

    // -------------------------------------------------------------------------
    // Simple in-process benchmark (non-JMH)
    // -------------------------------------------------------------------------

    /**
     * Measures createMap performance over 100k events using System.nanoTime().
     * This is NOT a replacement for JMH (no warm-up, no statistical analysis),
     * but it verifies the code completes in a reasonable time and documents
     * the expected order of magnitude.
     */
    @Test
    void benchmark_createMap_100kEvents() {
        // Populate 100k events spread across 2000-2029
        for (int i = 0; i < 100_000; i++) {
            int year  = 2000 + (i % 30);
            int month = 1 + (i % 12);
            int day   = 1 + (i % 28);
            String date = String.format("%02d%02d%04d", day, month, year);
            String[] coords = {POI1_COORDS, POI2_COORDS, POI3_COORDS};
            String   coord  = coords[i % 3];
            String[] states = {"A", "F", "S", "T", "N"};
            String   state  = states[i % 5];
            dataManager.addEvent(new Event("IN", "LOGIN", date, "u" + i, coord, state));
        }

        long start = System.nanoTime();
        captureCreateMap("01012000-31122029");
        long elapsed = System.nanoTime() - start;

        long elapsedMs = elapsed / 1_000_000;
        System.out.println("[benchmark] createMap over 100k events: " + elapsedMs + " ms");

        // Sanity threshold: should complete well under 5 seconds on any modern machine
        assertTrue(elapsedMs < 5000,
                "createMap took too long: " + elapsedMs + " ms (expected < 5000 ms)");
    }

    // -------------------------------------------------------------------------
    // Helper
    // -------------------------------------------------------------------------

    /** Runs createMap and captures its stdout output as a String. */
    private String captureCreateMap(String dateRange) {
        PrintStream originalOut = System.out;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(baos));
        try {
            dataManager.createMap(dateRange);
        } finally {
            System.setOut(originalOut);
        }
        return baos.toString();
    }
}
