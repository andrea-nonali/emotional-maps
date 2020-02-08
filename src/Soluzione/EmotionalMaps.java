package Soluzione;

//NONALI ANDREA 733636 --> Project Manager
//SCOLARI GIANLUCA 734624
//ENEI STEFANO 7297382

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
* MAIN METHOD
*/

public class EmotionalMaps {

    public static void main(String[] args) throws IOException, InterruptedException {
        DataManager dataManager = new DataManager();
        TxtReader textReader = new TxtReader(dataManager);

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        while (true){

            System.out.println("---> Inserisci il percorso del file comandi.txt. Digita 0 per terminare il programma <---");

            String path = reader.readLine();

            if(path.equals("0"))
                break;

            textReader.readCommands(path);

            Thread.sleep(1000);


        }
    }
}
