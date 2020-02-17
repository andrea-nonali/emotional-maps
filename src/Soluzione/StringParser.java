package Soluzione;

import java.util.ArrayList;
import java.util.HashMap;

/**
*Classe usata per fare parsing delle stringhe di eventi
*/

public class StringParser {

    /**
    *il metodo taglia i pezzi di strigna non utili
    */
    
    protected static String[] cutCommand(String command) {

        String[] commandAndFile = new String[2];

        if(command.substring(0, 6).equals("import")) {

            commandAndFile[0]=(command.substring(0, 6));
            commandAndFile[1]=command.substring(7,(command.length()-1));
            System.out.println("comando tagliato: " + commandAndFile[0]);
            System.out.println("valore comando tagliato: " + commandAndFile[1]);
        }
        else if(command.substring(0,10).equals("create_map")) {

            commandAndFile[0]=(command.substring(0, 10));
            commandAndFile[1]=(command.substring(11,(command.length()-1)));

            System.out.println("comando tagliato: " + commandAndFile[0]);
            System.out.println("valore comando tagliato: " + commandAndFile[1] + "\n");
        }

        else {
            System.out.println("ERRORE: il file contiene una funzione non riconosciuta:" + command
                    + "\nle uniche funzioni riconosciute sono " +
                    "import(nomefile.txt) e create_map(StartData-EndData)" + "\n");

            commandAndFile[0] = "ERR";
        }

        return commandAndFile;
    }

    /**
    *Ritorna un evento data una stringa del file dati.txt
    */
    
    protected static Event parseStringToEvent(String toParse){

        Event returnEvent;
        ArrayList<String> eventFields = new ArrayList<>();
        int iter = 0;
        String eventFieldValue = "";

        for(int eventField = 0; eventField< 6; eventField++){
            while(iter < toParse.length() && !toParse.substring(iter,iter+1).equals(" ")){
                eventFieldValue += toParse.substring(iter,iter+1);
                iter++;
            }
            eventFields.add(eventFieldValue);
            eventFieldValue = "";
            iter++;
        }

        /*for(String s: eventFields)
            if(!s.equals(""))
            System.out.println(s);

        System.out.println();*/

        //creo evento
        returnEvent = createEvent(eventFields);
        return returnEvent;
    }

    /**
    *Questo metodo prende una stringa e ne ritorna le coordinate
    */

    protected static float[] parseCoordinates(String coordinatesValue){

        float[] coordinates = new float[2];
        String firstNumberValue = "";
        String secondNumberValue = "";
        int iter = 0;

        for(int i = 0; i< 2; i++){
            while(iter < coordinatesValue.length() && !coordinatesValue.substring(iter,iter+1).equals(",")) {
                if (i == 0)
                    firstNumberValue += coordinatesValue.substring(iter, iter + 1);
                else
                    secondNumberValue += coordinatesValue.substring(iter, iter + 1);
                iter++;
            }
            iter++;
        }

        coordinates[0] = Float.parseFloat(firstNumberValue);
        coordinates[1] = Float.parseFloat(secondNumberValue);
        return coordinates;
    }

    /**
    Data una stringa di date presa dal comando create_map() ne ritorna un hasmap con data di inzio e di fine
    */
    protected static HashMap<String, Event> parseStringToUpperLowerDate(String dateValue){

        HashMap<String, Event> dates = new HashMap<>();

        String lowerBound = dateValue.substring(0,8);
        String upperBound = dateValue.substring(9,17);

        Event lowerBoundEvent = new Event(lowerBound);
        Event upperdBoundEvent = new Event(upperBound);

        dates.put("LOWERBOUND", lowerBoundEvent);
        dates.put("UPPERBOUND", upperdBoundEvent);

        return dates;
    }

    
    private static Event createEvent(ArrayList<String> eventFields){

        Event returnEvent;

        if(!(eventFields.get(0).equals("IN") || eventFields.get(0).equals("OUT"))) {
            System.err.println("Evento non riconosciuto da String Parser. Non verrà aggiunto \n");
            returnEvent = null;
        }
        else
            returnEvent = new Event(eventFields.get(0), eventFields.get(1),eventFields.get(2),
                    eventFields.get(3),eventFields.get(4),eventFields.get(5));

        return returnEvent;
    }
}
