package emotionalmaps;

import org.junit.jupiter.api.Test;
import java.util.HashMap;
import static org.junit.jupiter.api.Assertions.*;

class StringParserTest {

    // -------------------------------------------------------------------------
    // cutCommand
    // -------------------------------------------------------------------------

    @Test
    void cutCommand_import_extractsFileNameCorrectly() {
        String[] result = StringParser.cutCommand("import(dati.txt)");
        assertEquals("import",   result[0]);
        assertEquals("dati.txt", result[1]);
    }

    @Test
    void cutCommand_createMap_extractsDateRange() {
        String[] result = StringParser.cutCommand("create_map(01012020-31122020)");
        assertEquals("create_map",      result[0]);
        assertEquals("01012020-31122020", result[1]);
    }

    @Test
    void cutCommand_unknown_returnsERR() {
        String[] result = StringParser.cutCommand("delete_map(foo)");
        assertEquals("ERR", result[0]);
    }

    // -------------------------------------------------------------------------
    // parseStringToEvent
    // -------------------------------------------------------------------------

    @Test
    void parseStringToEvent_validLine_returnsEvent() {
        // Format: registrationStatus loginStatus date userId coordinates emotionalState
        Event e = StringParser.parseStringToEvent("IN LOGIN 01012021 usr01 45.464,9.190 A");
        assertNotNull(e);
        assertEquals(1,    e.getDay());
        assertEquals(1,    e.getMonth());
        assertEquals(2021, e.getYear());
        assertEquals("POI1", e.getPoi());
        assertEquals(EmotionalState.ANGRY, e.getEmotionalState());
        assertTrue(e.isRegistered());
        assertTrue(e.isLoggedIn());
    }

    @Test
    void parseStringToEvent_outStatus() {
        Event e = StringParser.parseStringToEvent("OUT LOGOUT 15032020 user2 45.473,9.173 F");
        assertNotNull(e);
        assertFalse(e.isRegistered());
        assertFalse(e.isLoggedIn());
        assertEquals(EmotionalState.HAPPY, e.getEmotionalState());
    }

    @Test
    void parseStringToEvent_unrecognisedStatus_returnsNull() {
        Event e = StringParser.parseStringToEvent("MAYBE LOGIN 01012021 usr01 45.464,9.190 A");
        assertNull(e);
    }

    @Test
    void parseStringToEvent_tooFewFields_returnsNull() {
        Event e = StringParser.parseStringToEvent("IN LOGIN 01012021");
        assertNull(e);
    }

    @Test
    void parseStringToEvent_extraWhitespace_handled() {
        // split("\\s+") should handle multiple spaces
        Event e = StringParser.parseStringToEvent("IN  LOGIN  01012021  usr01  45.464,9.190  A");
        assertNotNull(e);
    }

    // -------------------------------------------------------------------------
    // parseCoordinates
    // -------------------------------------------------------------------------

    @Test
    void parseCoordinates_poi1Coords() {
        float[] coords = StringParser.parseCoordinates("45.464,9.190");
        assertEquals(45.464f, coords[0], 0.001f);
        assertEquals(9.190f,  coords[1], 0.001f);
    }

    @Test
    void parseCoordinates_withSpaces() {
        float[] coords = StringParser.parseCoordinates("45.464, 9.190");
        assertEquals(45.464f, coords[0], 0.001f);
        assertEquals(9.190f,  coords[1], 0.001f);
    }

    // -------------------------------------------------------------------------
    // parseDateRange
    // -------------------------------------------------------------------------

    @Test
    void parseDateRange_returnsCorrectBounds() {
        HashMap<String, Event> bounds = StringParser.parseDateRange("01012020-31122020");
        Event lower = bounds.get("LOWERBOUND");
        Event upper = bounds.get("UPPERBOUND");

        assertNotNull(lower);
        assertNotNull(upper);

        assertEquals(1,    lower.getDay());
        assertEquals(1,    lower.getMonth());
        assertEquals(2020, lower.getYear());

        assertEquals(31,   upper.getDay());
        assertEquals(12,   upper.getMonth());
        assertEquals(2020, upper.getYear());
    }
}
