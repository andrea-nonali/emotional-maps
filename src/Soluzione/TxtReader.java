package Soluzione;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**LA CLASSE LEGGE I FILE DI TESTO CON INDICAZIONI E LI AGGIUNGE AL DATAMANAGER
Sfrutta StringParser per tagliare le parti inutili dei comandi e creare eventi
*/

public class TxtReader {
	private DataManager dataManager;
	private ArrayList<String> commands;

	protected TxtReader(DataManager dm) {
		this.dataManager = dm;
		this.commands = new ArrayList<>();
	}

	/**
	*metodo che legge il file comandi.txt e li aggiunge ad un arrayList
	*/
	
	protected void readCommands(String commandsFilePath) {

		BufferedReader in;
		File commandsFile = new File(commandsFilePath);

		try {
			in = new BufferedReader(new FileReader(commandsFile));
		} catch (FileNotFoundException e) {
			System.err.println("Errore di lettura del file."
					+ " controlla il percorso \n");
			return;
		}

		String command;
		try {
			while((command=in.readLine())!= null) {
				commands.add(command);
			}
		}catch(IOException exc) {
			System.err.println("Errore in lettura del file");
		}
		execCommands();
	}

	/**
	*metodo che esegue tutti i comandi di comandi.txt
	*/
	
	private void execCommands() {

		String[] command_and_value;

		for(int i = 0; i < commands.size(); i++) {

			command_and_value = StringParser.cutCommand(commands.get(i));

			switch(command_and_value[0]) {

			case("import"):
				try {
					//algoritmo per importare
					addEventsToDataManger(command_and_value[1]);
				} catch (IOException e) {
					System.err.println("Errore di lettura del file");
				}
				break;

			case("create_map"):
				this.dataManager.createMap(command_and_value[1]);
				break;

			default:
				System.err.println("comando non riconosciuto durante il taglio perchè errato\n");
			}
		}
	}

	/**Il metodo aggiunge gli eventi al dataManager
	*
	*/
	
	private void addEventsToDataManger(String fileName) throws IOException {

		File f = new File(fileName);
		BufferedReader readFile = null;

		try {
			readFile= new BufferedReader(new FileReader(f));
		} catch (FileNotFoundException e) {
			System.err.println("Il percorso del file " + fileName + " non è stato trovato nel filesystem \n" );
		}

		Event event;
		String eventValue;

		while((eventValue = readFile.readLine())!=null) {
			event = StringParser.parseStringToEvent(eventValue);
			if(event!=null)
				this.dataManager.addEvent(event);
		}
	}
}
