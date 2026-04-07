package emotionalmaps;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Generates random events and writes them to a file.
 * The output path can be supplied as args[0]; if omitted, "test-data.txt" in the
 * current working directory is used as a fallback.
 */
public class RandomEventGenerator {

    private static final String ALPHA_NUMERIC_STRING = "abcdefghijklmnopqrstuvwxyz0123456789";

    public static String randomUserId() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            int character = (int) (Math.random() * ALPHA_NUMERIC_STRING.length());
            builder.append(ALPHA_NUMERIC_STRING.charAt(character));
        }
        builder.append(" ");
        return builder.toString();
    }

    public static String randomLoginStatus() {
        return Math.round(Math.random() * 100) > 50 ? "LOGIN " : "LOGOUT ";
    }

    public static String randomRegistrationStatus() {
        return Math.round(Math.random() * 100) > 50 ? "OUT " : "IN ";
    }

    public static String randomCoordinates() {
        long i = Math.round(Math.random() * 100);
        if (i > 30 && i < 66) return "45.473,9.173 ";
        if (i < 30)           return "45.464,9.190 ";
        return "45.458,9.181 "; // i >= 66 (and the i==30 edge)
    }

    public static String randomEmotionalState() {
        double randomDouble = Math.random();
        randomDouble = randomDouble * 50 + 1;
        int randomInt = (int) randomDouble;

        if (randomInt <= 10) return "A";
        if (randomInt <= 20) return "F";
        if (randomInt <= 30) return "T";
        if (randomInt <= 40) return "N";
        return "S";
    }

    public static String randomDate() {
        int day   = (int) (Math.random() * 20 + 10);
        int month = (int) (Math.random() *  3 + 10);
        int year  = (int) (Math.random() * 30 + 2000);
        return day + Integer.toString(month) + year + " ";
    }

    public static void main(String[] args) {
        String outputPath = (args != null && args.length > 0)
                ? args[0]
                : System.getProperty("user.dir") + "/test-data.txt";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath))) {
            for (int i = 0; i < 100_000; i++) {
                StringBuilder sb = new StringBuilder();
                sb.append(randomRegistrationStatus());
                sb.append(randomLoginStatus());
                sb.append(randomDate());
                sb.append(randomUserId());
                sb.append(randomCoordinates());
                sb.append(randomEmotionalState());
                writer.write(sb.toString());
                writer.newLine();
            }
            System.out.println("Generated 100,000 events → " + outputPath);
        } catch (IOException e) {
            System.err.println("Failed to write output file: " + e.getMessage());
        }
    }
}
