package emotionalmaps;

import java.util.*;

/**
 * Stores and analyses emotional-map data.
 *
 * Data structure: HashMap&lt;Integer, TreeSet&lt;Event&gt;&gt;
 *   - The outer HashMap maps a calendar year to a TreeSet of Events for that year.
 *   - The TreeSet keeps events ordered by date (O(log n) insertion).
 *   - Map creation iterates the relevant year slices in O(n + k), where n is the
 *     number of events in the date range and k is the number of years spanned.
 */
public class DataManager {

    private static final int POI_COUNT = 3;

    /** Maps year → sorted set of events for that year. */
    private HashMap<Integer, TreeSet<Event>> dataCollector;

    /**
     * Creates a new DataManager and pre-populates years 2000–2030 so that the
     * common case of inserting events needs no extra HashMap put calls.
     */
    public DataManager() {
        this.dataCollector = new HashMap<>();
        for (int i = 2000; i <= 2030; i++) {
            this.dataCollector.put(i, new TreeSet<>(Event::compareTo));
        }
    }

    /**
     * Adds an event to the store. O(log m) where m is the number of events already
     * stored for the event's year.
     *
     * If the year is not yet in the store (possible for years outside 2000–2030),
     * a new TreeSet is created on demand.
     */
    public void addEvent(Event event) {
        dataCollector.computeIfAbsent(event.getYear(), y -> new TreeSet<>(Event::compareTo))
                     .add(event);
    }

    /**
     * Checks whether there is at least one event in the TreeSets for the lower-bound
     * and upper-bound years. O(1).
     */
    private boolean areThereEvents(Event lowerBound, Event upperBound) {
        TreeSet<Event> lowerSet = dataCollector.get(lowerBound.getYear());
        TreeSet<Event> upperSet = dataCollector.get(upperBound.getYear());
        return lowerSet != null && !lowerSet.isEmpty()
            && upperSet != null && !upperSet.isEmpty();
    }

    /**
     * Creates the emotional map for the given date-range string (format "DDMMYYYY-DDMMYYYY").
     * Prints per-POI percentages of each emotional state to stdout.
     */
    public void createMap(String dateValue) {

        long startTime = System.currentTimeMillis();

        HashMap<String, Event> bounds = StringParser.parseDateRange(dateValue);
        Event lowerBoundEvt = bounds.get("LOWERBOUND");
        Event upperBoundEvt = bounds.get("UPPERBOUND");

        if (!areThereEvents(lowerBoundEvt, upperBoundEvt)) {
            System.err.println("Cannot create map: no events found for the specified date range.\n");
            return;
        }

        /*
         * emotionalData.get(0) → POI1 counts
         * emotionalData.get(1) → POI2 counts
         * emotionalData.get(2) → POI3 counts
         *
         * totalEvents[i] avoids recomputing sums over the map during percentage output.
         */
        List<Map<EmotionalState, Integer>> emotionalData = new ArrayList<>(POI_COUNT);
        for (int i = 0; i < POI_COUNT; i++) {
            Map<EmotionalState, Integer> poiMap = new HashMap<>();
            for (EmotionalState state : EmotionalState.toArray()) {
                poiMap.put(state, 0);
            }
            emotionalData.add(poiMap);
        }
        int[] totalEvents = new int[POI_COUNT];

        if (lowerBoundEvt.getYear() == upperBoundEvt.getYear()) {
            mapEventsWithSameYear(lowerBoundEvt, upperBoundEvt, emotionalData, totalEvents);
        } else if (lowerBoundEvt.getYear() < upperBoundEvt.getYear()) {
            mapEventsWithDifferentYears(lowerBoundEvt, upperBoundEvt, emotionalData, totalEvents);
        } else {
            System.err.println("Cannot map: start date is after end date.");
            return;
        }

        System.out.println("Total events at POI1: " + totalEvents[0]);
        System.out.println("Total events at POI2: " + totalEvents[1]);
        System.out.println("Total events at POI3: " + totalEvents[2] + "\n");

        for (int i = 1; i <= POI_COUNT; i++) {
            System.out.println(buildPoiResult(i, emotionalData.get(i - 1), totalEvents[i - 1]));
        }

        System.out.println();

        long endTime = System.currentTimeMillis();
        System.out.println("Start time (ms): " + startTime);
        System.out.println("End time (ms):   " + endTime);
        System.out.println("Map creation time (ms): " + (endTime - startTime));
        System.out.println("Map creation time (s):  " + (endTime - startTime) / 1000);
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private String buildPoiResult(int poiNumber, Map<EmotionalState, Integer> counts, int total) {
        StringBuilder sb = new StringBuilder("POI").append(poiNumber).append(" ---> ");
        sb.append(pct(total, counts.get(EmotionalState.ANGRY))).append("% A ");
        sb.append(pct(total, counts.get(EmotionalState.HAPPY))).append("% F ");
        sb.append(pct(total, counts.get(EmotionalState.SURPRISED))).append("% S ");
        sb.append(pct(total, counts.get(EmotionalState.SAD))).append("% T ");
        sb.append(pct(total, counts.get(EmotionalState.NEUTRAL))).append("% N");
        return sb.toString();
    }

    /**
     * Computes the percentage of {@code count} events out of {@code total}.
     * Returns 0 when count is zero (no events of that type at this POI).
     */
    private long pct(int total, int count) {
        if (count == 0) return 0L;
        return Math.round(100.0 * count / total);
    }

    /**
     * Accumulates emotional-state counts for events in a single-year date range.
     */
    private void mapEventsWithSameYear(Event lowerBoundEvt, Event upperBoundEvt,
                                       List<Map<EmotionalState, Integer>> emotionalData,
                                       int[] totalEvents) {

        TreeSet<Event> eventContainer = this.dataCollector.get(lowerBoundEvt.getYear());
        if (eventContainer == null) return;

        for (Event iterEvent : eventContainer) {
            if (iterEvent.isAfter(upperBoundEvt)) break;
            if (iterEvent.isBefore(lowerBoundEvt)) continue;
            accumulateEvent(iterEvent, emotionalData, totalEvents);
        }
    }

    /**
     * Accumulates emotional-state counts for events spanning multiple years.
     */
    private void mapEventsWithDifferentYears(Event lowerBoundEvt, Event upperBoundEvt,
                                             List<Map<EmotionalState, Integer>> emotionalData,
                                             int[] totalEvents) {

        for (int year = lowerBoundEvt.getYear(); year <= upperBoundEvt.getYear(); year++) {
            TreeSet<Event> current = this.dataCollector.get(year);
            if (current == null) continue;

            for (Event iterEvent : current) {
                if (iterEvent.isAfter(upperBoundEvt)) break;
                if (year == lowerBoundEvt.getYear() && iterEvent.isBefore(lowerBoundEvt)) continue;
                accumulateEvent(iterEvent, emotionalData, totalEvents);
            }
        }
    }

    /**
     * Increments the count for the event's emotional state in the correct POI bucket.
     * UNDEFINED events are silently discarded.
     */
    private void accumulateEvent(Event event,
                                  List<Map<EmotionalState, Integer>> emotionalData,
                                  int[] totalEvents) {
        int idx;
        switch (event.getPoi()) {
            case "POI1": idx = 0; break;
            case "POI2": idx = 1; break;
            case "POI3": idx = 2; break;
            default: return; // UNDEFINED — discard
        }
        Map<EmotionalState, Integer> poiMap = emotionalData.get(idx);
        poiMap.put(event.getEmotionalState(), poiMap.get(event.getEmotionalState()) + 1);
        totalEvents[idx]++;
    }
}
