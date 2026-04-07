package emotionalmaps;

public enum EmotionalState {
    ANGRY, HAPPY, SURPRISED, SAD, NEUTRAL;

    public static EmotionalState fromCode(String code) {
        switch (code) {
            case "A": return ANGRY;
            case "F": return HAPPY;
            case "S": return SURPRISED;
            case "T": return SAD;
            case "N": return NEUTRAL;
            default: return null;
        }
    }

    private static final EmotionalState[] VALUES = {ANGRY, HAPPY, SURPRISED, SAD, NEUTRAL};

    /** Returns the fixed set of emotional states. The returned array is shared — do not mutate it. */
    public static EmotionalState[] toArray() {
        return VALUES;
    }
}
