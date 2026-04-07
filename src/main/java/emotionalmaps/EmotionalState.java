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

    public static EmotionalState[] toArray() {
        return new EmotionalState[]{ANGRY, HAPPY, SURPRISED, SAD, NEUTRAL};
    }
}
