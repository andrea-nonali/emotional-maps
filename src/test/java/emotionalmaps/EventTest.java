package emotionalmaps;

import org.junit.jupiter.api.Test;
import java.util.TreeSet;
import static org.junit.jupiter.api.Assertions.*;

class EventTest {

    // Helper: build a minimal Event with only date set (uses single-arg constructor)
    private Event dateEvent(String date) {
        return new Event(date);
    }

    // Helper: full event at POI1 coordinates
    private Event fullEvent(String date, String poi, String emotion) {
        // Registration=IN, Login=LOGIN, userId=user1
        String coords;
        switch (poi) {
            case "POI1": coords = "45.464,9.190"; break;
            case "POI2": coords = "45.473,9.173"; break;
            case "POI3": coords = "45.458,9.181"; break;
            default:     coords = "0.0,0.0"; break;
        }
        return new Event("IN", "LOGIN", date, "user1", coords, emotion);
    }

    // -------------------------------------------------------------------------
    // setDate
    // -------------------------------------------------------------------------

    @Test
    void setDate_valid_parsesCorrectly() {
        Event e = dateEvent("15032021");
        assertEquals(15,   e.getDay());
        assertEquals(3,    e.getMonth());
        assertEquals(2021, e.getYear());
    }

    @Test
    void setDate_wrongLength_doesNotThrow() {
        // Should print an error but not throw
        assertDoesNotThrow(() -> dateEvent("1503"));
    }

    // -------------------------------------------------------------------------
    // setPoi
    // -------------------------------------------------------------------------

    @Test
    void setPoi_poi1_coordinates() {
        Event e = fullEvent("01012020", "POI1", "A");
        assertEquals("POI1", e.getPoi());
    }

    @Test
    void setPoi_poi2_coordinates() {
        Event e = fullEvent("01012020", "POI2", "A");
        assertEquals("POI2", e.getPoi());
    }

    @Test
    void setPoi_poi3_coordinates() {
        Event e = fullEvent("01012020", "POI3", "A");
        assertEquals("POI3", e.getPoi());
    }

    @Test
    void setPoi_outsideAllPois_isUndefined() {
        Event e = fullEvent("01012020", "NONE", "A");
        assertEquals("UNDEFINED", e.getPoi());
    }

    // -------------------------------------------------------------------------
    // isAfter / isBefore
    // -------------------------------------------------------------------------

    @Test
    void isAfter_laterYear() {
        Event earlier = dateEvent("01012020");
        Event later   = dateEvent("01012021");
        assertTrue(later.isAfter(earlier));
        assertFalse(earlier.isAfter(later));
    }

    @Test
    void isAfter_laterMonth_sameYear() {
        Event earlier = dateEvent("01012021");
        Event later   = dateEvent("01022021");
        assertTrue(later.isAfter(earlier));
        assertFalse(earlier.isAfter(later));
    }

    @Test
    void isAfter_laterDay_sameMonthYear() {
        Event earlier = dateEvent("01012021");
        Event later   = dateEvent("15012021");
        assertTrue(later.isAfter(earlier));
        assertFalse(earlier.isAfter(later));
    }

    @Test
    void isAfter_sameDate_returnsFalse() {
        Event e1 = dateEvent("15032021");
        Event e2 = dateEvent("15032021");
        assertFalse(e1.isAfter(e2));
        assertFalse(e2.isAfter(e1));
    }

    @Test
    void isBefore_symmetricWithIsAfter() {
        Event earlier = dateEvent("01012020");
        Event later   = dateEvent("01012021");
        assertTrue(earlier.isBefore(later));
        assertFalse(later.isBefore(earlier));
    }

    @Test
    void isBefore_sameDate_returnsFalse() {
        Event e1 = dateEvent("15032021");
        Event e2 = dateEvent("15032021");
        assertFalse(e1.isBefore(e2));
    }

    // -------------------------------------------------------------------------
    // compareTo — TreeSet ordering / deduplication prevention
    // -------------------------------------------------------------------------

    @Test
    void compareTo_earlierEventIsSmaller() {
        Event earlier = dateEvent("01012020");
        Event later   = dateEvent("01012021");
        assertTrue(earlier.compareTo(later) < 0);
        assertTrue(later.compareTo(earlier) > 0);
    }

    @Test
    void compareTo_sameDate_returnsPositive_preventsDuplication() {
        // Two events on the same day must NOT compare to 0 — otherwise TreeSet drops one
        Event e1 = fullEvent("15032021", "POI1", "A");
        Event e2 = fullEvent("15032021", "POI1", "F");
        assertNotEquals(0, e1.compareTo(e2),
                "compareTo must not return 0 for same-day events (would cause TreeSet deduplication)");
    }

    @Test
    void treeSet_acceptsMultipleEventsOnSameDay() {
        TreeSet<Event> set = new TreeSet<>(Event::compareTo);
        set.add(fullEvent("15032021", "POI1", "A"));
        set.add(fullEvent("15032021", "POI1", "F"));
        set.add(fullEvent("15032021", "POI2", "S"));
        assertEquals(3, set.size(),
                "All same-day events must be stored (no deduplication)");
    }
}
