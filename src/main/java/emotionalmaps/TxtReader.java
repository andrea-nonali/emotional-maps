package emotionalmaps;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Reads command files, parses each command, and delegates to DataManager.
 * Commands supported: import(filename.txt) and create_map(StartDate-EndDate).
 */
public class TxtReader {

    private DataManager dataManager;
    private ArrayList<String> commands;

    protected TxtReader(DataManager dm) {
        this.dataManager = dm;
        this.commands = new ArrayList<>();
    }

    /**
     * Reads the command file at the given path and executes each command.
     */
    protected void readCommands(String commandsFilePath) {
        commands.clear(); // reset so repeated calls do not re-execute old commands
        try (BufferedReader in = new BufferedReader(new FileReader(commandsFilePath))) {
            String command;
            while ((command = in.readLine()) != null) {
                commands.add(command);
            }
        } catch (FileNotFoundException e) {
            System.err.println("File not found, check the path: " + commandsFilePath + "\n");
            return;
        } catch (IOException e) {
            System.err.println("Error reading file: " + commandsFilePath);
            return;
        }

        execCommands();
    }

    /**
     * Executes all commands loaded from the command file.
     */
    private void execCommands() {
        for (String rawCommand : commands) {
            String[] commandAndValue = StringParser.cutCommand(rawCommand);

            switch (commandAndValue[0]) {
                case "import":
                    try {
                        addEventsToDataManager(commandAndValue[1]);
                    } catch (IOException e) {
                        System.err.println("Error reading data file: " + commandAndValue[1]);
                    }
                    break;

                case "create_map":
                    this.dataManager.createMap(commandAndValue[1]);
                    break;

                default:
                    System.err.println("Unrecognised command after parsing: " + commandAndValue[0] + "\n");
            }
        }
    }

    /**
     * Reads events from the given data file and adds them to the DataManager.
     * If the file cannot be opened the method logs an error and returns without crashing.
     */
    private void addEventsToDataManager(String fileName) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Event event = StringParser.parseStringToEvent(line);
                if (event != null) {
                    this.dataManager.addEvent(event);
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("Data file not found: " + fileName);
        }
    }
}
