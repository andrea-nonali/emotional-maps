package emotionalmaps;

import java.util.HashMap;

/**
 * Utility class for parsing event strings, coordinates, commands, and date ranges.
 */
public class StringParser {

    /**
     * Splits a command string into [commandName, argument].
     * Recognised commands: "import" and "create_map".
     * Returns commandAndFile[0] = "ERR" for unrecognised commands.
     */
    protected static String[] cutCommand(String command) {
        String[] commandAndFile = new String[2];

        if (command.startsWith("import")) {
            commandAndFile[0] = command.substring(0, 6);
            commandAndFile[1] = command.substring(7, command.length() - 1);
        } else if (command.startsWith("create_map")) {
            commandAndFile[0] = command.substring(0, 10);
            commandAndFile[1] = command.substring(11, command.length() - 1);
        } else {
            System.err.println("ERROR: the file contains an unrecognised function: " + command
                    + "\nThe only recognised functions are "
                    + "import(filename.txt) and create_map(StartDate-EndDate)\n");
            commandAndFile[0] = "ERR";
        }

        return commandAndFile;
    }

    /**
     * Parses a whitespace-separated event line and returns the corresponding Event,
     * or null if the line is malformed or the registration status is unrecognised.
     */
    protected static Event parseStringToEvent(String toParse) {
        String[] fields = toParse.trim().split("\\s+");
        if (fields.length < 6) {
            System.err.println("Malformed event line (fewer than 6 fields): " + toParse);
            return null;
        }
        return createEvent(fields);
    }

    /**
     * Parses a "lat,lon" coordinate string and returns a float[]{lat, lon}.
     */
    protected static float[] parseCoordinates(String coordinatesValue) {
        String[] parts = coordinatesValue.split(",");
        float[] coordinates = new float[2];
        coordinates[0] = Float.parseFloat(parts[0].trim());
        coordinates[1] = Float.parseFloat(parts[1].trim());
        return coordinates;
    }

    /**
     * Parses a date-range string of the form "DDMMYYYY-DDMMYYYY" and returns a
     * HashMap with "LOWERBOUND" and "UPPERBOUND" Event keys.
     */
    protected static HashMap<String, Event> parseDateRange(String dateValue) {
        HashMap<String, Event> dates = new HashMap<>();

        String lowerBound = dateValue.substring(0, 8);
        String upperBound = dateValue.substring(9, 17);

        dates.put("LOWERBOUND", new Event(lowerBound));
        dates.put("UPPERBOUND", new Event(upperBound));

        return dates;
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private static Event createEvent(String[] fields) {
        if (!(fields[0].equals("IN") || fields[0].equals("OUT"))) {
            System.err.println("Unrecognised event; it will not be added.\n");
            return null;
        }
        return new Event(fields[0], fields[1], fields[2], fields[3], fields[4], fields[5]);
    }
}
