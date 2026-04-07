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
     * a new TreeSet is created and the event is added to it.
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

        // Parse the date range into lower/upper bound sentinel events
        HashMap<String, Event> bounds = StringParser.parseDateRange(dateValue);
        Event lowerBoundEvt = bounds.get("LOWERBOUND");
        Event upperBoundEvt = bounds.get("UPPERBOUND");

        if (!areThereEvents(lowerBoundEvt, upperBoundEvt)) {
            System.err.println("Cannot create map: no events found for the specified date range.\n");
            return;
        }

        /*
         * emotionalData[0] → POI1 counts
         * emotionalData[1] → POI2 counts
         * emotionalData[2] → POI3 counts
         *
         * totalEvents[i] avoids recomputing sums over the map during percentage output.
         */
        @SuppressWarnings("unchecked")
        HashMap<EmotionalState, Integer>[] emotionalData = new HashMap[3];
        for (int i = 0; i < 3; i++) {
            emotionalData[i] = new HashMap<>();
        }
        int[] totalEvents = new int[3];

        // Initialise all emotional-state counters to zero for every POI
        EmotionalState[] emotionalStates = EmotionalState.toArray();
        for (HashMap<EmotionalState, Integer> hashMap : emotionalData) {
            for (EmotionalState state : emotionalStates) {
                hashMap.put(state, 0);
            }
        }

        // Populate counts
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

        // Print percentage of each emotional state per POI
        for (int i = 1; i <= 3; i++) {
            int idx = i - 1;

            double angryRatio    = computeRatio(totalEvents[idx], emotionalData[idx].get(EmotionalState.ANGRY));
            double happyRatio    = computeRatio(totalEvents[idx], emotionalData[idx].get(EmotionalState.HAPPY));
            double surprisedRatio = computeRatio(totalEvents[idx], emotionalData[idx].get(EmotionalState.SURPRISED));
            double sadRatio      = computeRatio(totalEvents[idx], emotionalData[idx].get(EmotionalState.SAD));
            double neutralRatio  = computeRatio(totalEvents[idx], emotionalData[idx].get(EmotionalState.NEUTRAL));

            String result = "POI" + i + " ---> ";
            result += formatPct(angryRatio)    + "% A ";
            result += formatPct(happyRatio)    + "% F ";
            result += formatPct(surprisedRatio) + "% S ";
            result += formatPct(sadRatio)      + "% T ";
            result += formatPct(neutralRatio)  + "% N ";

            System.out.println(result);
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

    /**
     * Returns totalEvents / emotionalStateCount, handling the zero-count case
     * to avoid NaN in the final percentage output.
     */
    private double computeRatio(int total, int count) {
        if (count == 0) return Double.POSITIVE_INFINITY; // => 100/Infinity == 0
        return (double) total / (double) count;
    }

    /** Converts a ratio back to a rounded percentage, returning 0 for NaN/Infinity. */
    private long formatPct(double ratio) {
        if (Double.isNaN(ratio) || Double.isInfinite(ratio)) return 0L;
        return Math.round(100.0 / ratio);
    }

    /**
     * Accumulates emotional-state counts for events in a single-year date range.
     * Iterates the TreeSet for that year and skips/breaks on out-of-range events.
     *
     * Uses {@link Event#isAfter} / {@link Event#isBefore} for correct boundary
     * comparisons (not compareTo, which has the TreeSet deduplication side-effect).
     */
    private void mapEventsWithSameYear(Event lowerBoundEvt, Event upperBoundEvt,
                                       HashMap<EmotionalState, Integer>[] emotionalData,
                                       int[] totalEvents) {

        TreeSet<Event> eventContainer = this.dataCollector.get(lowerBoundEvt.getYear());
        Iterator<Event> iterator = eventContainer.iterator();

        while (iterator.hasNext()) {
            Event iterEvent = iterator.next();

            // BUG FIX: use isAfter() instead of the broken compareTo()-based condition
            if (iterEvent.isAfter(upperBoundEvt)) break;
            if (iterEvent.isBefore(lowerBoundEvt)) continue;

            accumulateEvent(iterEvent, emotionalData, totalEvents);
        }
    }

    /**
     * Accumulates emotional-state counts for events spanning multiple years.
     * Iterates year-by-year; applies lower-bound filter only for the first year
     * and upper-bound break only for the last year.
     */
    private void mapEventsWithDifferentYears(Event lowerBoundEvt, Event upperBoundEvt,
                                             HashMap<EmotionalState, Integer>[] emotionalData,
                                             int[] totalEvents) {

        for (int year = lowerBoundEvt.getYear(); year <= upperBoundEvt.getYear(); year++) {
            TreeSet<Event> current = this.dataCollector.get(year);
            if (current == null) continue;

            Iterator<Event> iterator = current.iterator();

            while (iterator.hasNext()) {
                Event iterEvent = iterator.next();

                // BUG FIX: use isAfter() for the upper-bound break
                if (iterEvent.isAfter(upperBoundEvt)) break;

                // Apply lower-bound filter only in the starting year
                if (year == lowerBoundEvt.getYear() && iterEvent.isBefore(lowerBoundEvt)) continue;

                accumulateEvent(iterEvent, emotionalData, totalEvents);
            }
        }
    }

    /**
     * Increments the count for {@code iterEvent}'s emotional state in the correct
     * POI bucket.  UNDEFINED events are silently discarded.
     *
     * BUG FIX: POI2 now reads/writes emotionalData[1] and POI3 reads/writes
     * emotionalData[2] (the original code incorrectly read from emotionalData[0]
     * for both, causing wrong counts for POI2 and POI3).
     */
    private void accumulateEvent(Event iterEvent,
                                  HashMap<EmotionalState, Integer>[] emotionalData,
                                  int[] totalEvents) {
        switch (iterEvent.getPoi()) {
            case "POI1":
                emotionalData[0].put(iterEvent.getEmotionalState(),
                        emotionalData[0].get(iterEvent.getEmotionalState()) + 1);
                totalEvents[0]++;
                break;

            case "POI2":
                // BUG FIX: was incorrectly reading emotionalData[0] instead of emotionalData[1]
                emotionalData[1].put(iterEvent.getEmotionalState(),
                        emotionalData[1].get(iterEvent.getEmotionalState()) + 1);
                totalEvents[1]++;
                break;

            case "POI3":
                // BUG FIX: was incorrectly reading emotionalData[0] instead of emotionalData[2]
                emotionalData[2].put(iterEvent.getEmotionalState(),
                        emotionalData[2].get(iterEvent.getEmotionalState()) + 1);
                totalEvents[2]++;
                break;

            default:
                // UNDEFINED POI — discard silently
                break;
        }
    }
}
