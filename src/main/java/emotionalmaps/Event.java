package emotionalmaps;

import java.util.HashMap;

/**
 * Models a single recorded event with registration/login status, date, user ID,
 * point of interest (POI), and emotional state.
 */
public class Event implements Comparable<Event> {

    private boolean registrationStatus;
    private boolean loginStatus;
    private HashMap<String, Integer> date;
    private String userId;
    private String poi;
    private EmotionalState emotionalState;

    /**
     * Full constructor used to create events from parsed data.
     */
    public Event(String registrationStatus, String loginStatus, String date,
                 String userId, String coordinates, String emotionalState) {
        setRegistrationStatus(registrationStatus);
        setLoginStatus(loginStatus);
        setDate(date);
        this.userId = userId;
        setPoi(coordinates);
        setEmotionalState(emotionalState);
    }

    /**
     * Helper constructor used by StringParser to create lower/upper bound date events.
     */
    public Event(String date) {
        setDate(date);
    }

    public boolean isRegistered() {
        return registrationStatus;
    }

    public void setRegistrationStatus(String registrationStatus) {
        this.registrationStatus = registrationStatus.equals("IN");
    }

    public boolean isLoggedIn() {
        return loginStatus;
    }

    public void setLoginStatus(String loginStatus) {
        this.loginStatus = loginStatus.equals("LOGIN");
    }

    public int getDay() {
        return this.date.get("DAY");
    }

    public int getMonth() {
        return this.date.get("MONTH");
    }

    public int getYear() {
        return this.date.get("YEAR");
    }

    public void setDate(String data) {
        date = new HashMap<>();
        if (data.length() != 8) {
            System.err.println("Date format not supported by the system");
        } else {
            this.date.put("DAY", Integer.parseInt(data.substring(0, 2)));
            this.date.put("MONTH", Integer.parseInt(data.substring(2, 4)));
            this.date.put("YEAR", Integer.parseInt(data.substring(4, 8)));
        }
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPoi() {
        return poi;
    }

    /**
     * Determines the point of interest based on geographic coordinates.
     * Events that do not fall within any POI bounding box are marked UNDEFINED
     * and will be discarded during map creation.
     */
    public void setPoi(String coordinatesValue) {
        float[] coordinates = StringParser.parseCoordinates(coordinatesValue);

        // Square bounding box for POI1
        if ((coordinates[0] >= 45.459 && coordinates[0] <= 45.469) &&
                (coordinates[1] >= 9.185 && coordinates[1] <= 9.195)) {
            this.poi = "POI1";
        }
        // Square bounding box for POI2
        else if ((coordinates[0] >= 45.468 && coordinates[0] <= 45.478) &&
                (coordinates[1] >= 9.168 && coordinates[1] <= 9.178)) {
            this.poi = "POI2";
        }
        // Square bounding box for POI3
        else if ((coordinates[0] >= 45.453 && coordinates[0] <= 45.463) &&
                (coordinates[1] >= 9.176 && coordinates[1] <= 9.185)) {
            this.poi = "POI3";
        }
        // Outside all POI zones — will be discarded
        else {
            this.poi = "UNDEFINED";
        }
    }

    public EmotionalState getEmotionalState() {
        return emotionalState;
    }

    public void setEmotionalState(String emotionalState) {
        this.emotionalState = EmotionalState.fromCode(emotionalState);
    }

    /**
     * Compares two events by date (year, then month, then day).
     *
     * IMPORTANT: When the day values are equal, this method intentionally returns 1
     * (i.e. treats "this" as greater). This prevents the TreeSet from treating two events
     * on the same date as duplicates and discarding one. The TreeSet uses this comparator
     * to determine equality, so returning 0 for same-day events would cause silent data loss.
     *
     * Consequence: {@code compareTo} is NOT a strict ordering and must NOT be used for
     * date-range boundary checks. Use {@link #isAfter(Event)} and {@link #isBefore(Event)}
     * for those comparisons.
     *
     * Returns negative if this event is earlier, positive if later (or same day).
     */
    @Override
    public int compareTo(Event otherEvent) {
        int yearCmp = Integer.compare(this.getYear(), otherEvent.getYear());
        if (yearCmp != 0) return yearCmp;

        int monthCmp = Integer.compare(this.getMonth(), otherEvent.getMonth());
        if (monthCmp != 0) return monthCmp;

        // Always return 1 for equal days to prevent TreeSet deduplication
        if (this.getDay() >= otherEvent.getDay()) return 1;
        return -1;
    }

    /**
     * Returns true if this event's date is strictly after {@code other}'s date.
     * Uses a correct three-field comparison without the TreeSet deduplication trick.
     */
    boolean isAfter(Event other) {
        if (this.getYear() != other.getYear()) return this.getYear() > other.getYear();
        if (this.getMonth() != other.getMonth()) return this.getMonth() > other.getMonth();
        return this.getDay() > other.getDay();
    }

    /**
     * Returns true if this event's date is strictly before {@code other}'s date.
     * Uses a correct three-field comparison without the TreeSet deduplication trick.
     */
    boolean isBefore(Event other) {
        if (this.getYear() != other.getYear()) return this.getYear() < other.getYear();
        if (this.getMonth() != other.getMonth()) return this.getMonth() < other.getMonth();
        return this.getDay() < other.getDay();
    }
}
