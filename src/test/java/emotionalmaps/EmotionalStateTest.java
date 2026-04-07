package emotionalmaps;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EmotionalStateTest {

    @Test
    void fromCode_angry() {
        assertEquals(EmotionalState.ANGRY, EmotionalState.fromCode("A"));
    }

    @Test
    void fromCode_happy() {
        assertEquals(EmotionalState.HAPPY, EmotionalState.fromCode("F"));
    }

    @Test
    void fromCode_surprised() {
        assertEquals(EmotionalState.SURPRISED, EmotionalState.fromCode("S"));
    }

    @Test
    void fromCode_sad() {
        assertEquals(EmotionalState.SAD, EmotionalState.fromCode("T"));
    }

    @Test
    void fromCode_neutral() {
        assertEquals(EmotionalState.NEUTRAL, EmotionalState.fromCode("N"));
    }

    @Test
    void fromCode_unknown_returnsNull() {
        assertNull(EmotionalState.fromCode("X"));
        assertNull(EmotionalState.fromCode(""));
        assertNull(EmotionalState.fromCode("ANGRY"));
    }

    @Test
    void toArray_containsAllFive() {
        EmotionalState[] arr = EmotionalState.toArray();
        assertEquals(5, arr.length);
    }
}
