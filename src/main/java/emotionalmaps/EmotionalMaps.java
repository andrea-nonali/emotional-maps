package emotionalmaps;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Application entry point.
 * Prompts the user for a path to a command file and processes it in a loop.
 * Type "0" to exit.
 */
public class EmotionalMaps {

    public static void main(String[] args) throws IOException, InterruptedException {
        DataManager dataManager = new DataManager();
        TxtReader textReader = new TxtReader(dataManager);

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        while (true) {
            System.out.println("---> Enter the path to the commands file. Type 0 to exit. <---");

            String path = reader.readLine();

            if (path == null || path.equals("0")) break;

            textReader.readCommands(path);

            Thread.sleep(1000);
        }
    }
}
