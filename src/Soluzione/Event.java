package Soluzione;

import java.util.HashMap;

/**
*Classe usata per modellare un evento
*/

public class Event implements Comparable<Event>{
    private boolean statoRegistrazione;
    private boolean statoLogin;
    private HashMap<String, Integer> date;
    private String userId;
    private String poi;
    private EmotionalStates emotionalState;

    /**
    *Costruttore standard usato per creare eventi
    */
    
    public Event(String statoRegistrazione, String statoLogin, String date,
                 String userId,String coordinates, String emotionalState) {
        setStatoRegistrazione(statoRegistrazione);
        setStatoLogin(statoLogin);
        setDate(date);
        this.userId = userId;
        setPoi(coordinates);
        setEmotionalState(emotionalState);
    }

    /**
    *Costruttore helper usato da StringParser. Il suo scopo è quello di creare *una data di lower bound e una di upper bound.
    */
    
    public Event (String date){
        setDate(date);
    }

    public boolean isRegistrato() {
        return statoRegistrazione;
    }

    public void setStatoRegistrazione(String statoRegistrazione) {
        this.statoRegistrazione = statoRegistrazione.equals("IN") ? true : false;
    }

    public boolean isLoggato() {
        return statoLogin;
    }

    public void setStatoLogin(String statoLogin) {
        this.statoLogin = statoLogin.equals("LOGIN") ? true : false;
    }

    public int getDay(){
        return this.date.get("DAY");
    }

    public int getMonth(){
        return this.date.get("MONTH");
    }

    public int getYear(){
        return this.date.get("YEAR");
    }

    public void setDate(String data) {
        date = new HashMap<>();
        if(data.length()> 8 || data.length() < 8)
            System.out.println("Data non supportata dal sistema");
        else{
            this.date.put("DAY", Integer.parseInt(data.substring(0,2)));
            this.date.put("MONTH", Integer.parseInt(data.substring(2,4)));
            this.date.put("YEAR", Integer.parseInt(data.substring(4,8)));
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
    *Metodo usato per determinare il point of interest
    */
    
    public void setPoi(String coordinatesValue) {
    
        boolean hasPoi = false;
        float[] coordinates = StringParser.parseCoordinates(coordinatesValue);
        
        //Range quadrato POI1
        if ((coordinates[0] >= 45.459 && coordinates[0] <= 45.469) &&  (coordinates[1] >= 9.185 && coordinates[1] <= 9.195 )) {
            this.poi = "POI1";
            hasPoi = true;
        }
        
        //Range quadrato POI2
        if ((coordinates[0] >= 45.468 && coordinates[0] <= 45.478) &&  (coordinates[1] >= 9.168 && coordinates[1] <= 9.178)) {
            this.poi = "POI2";
            hasPoi = true;
        }
        
        //Range quadrato POI3
        if((coordinates[0] >= 45.453 && coordinates[0] <= 45.463) && (coordinates[1] >= 9.176 && coordinates[1] <= 9.185)) {
            this.poi = "POI3";
            hasPoi = true;
        }
        
        //Se non ha POI sarà considerato UNDEFINED sarà successivamente scartato 
        if(!hasPoi)
            this.poi = "UNDEFINED";
    }

    public EmotionalStates getEmotionalState() {
        return emotionalState;
    }

    public void setEmotionalState(String  emotionalState) {
        this.emotionalState = EmotionalStates.convertToEmotionalState(emotionalState);

    }


    /**Compare to:
      La data più recente sarà più grande.
      Se due date sono uguali quella più recente nel file di dati sarà considerata
      più grande.

      negative, if this object is less than the supplied object.
      zero, if this object is equal to the supplied object.
      positive, if this object is greater than the supplied object.
   */


    @Override
    public int compareTo(Event otherEvent) {
        //Se gli anni sono uguali ritorno compare del mese. Se i mesi sono uguali ritorno compare del giorno.
        if (compareYear(otherEvent) != 0)
            return compareYear(otherEvent);
        else
            if (compareMonth(otherEvent) != 0)
                return compareMonth(otherEvent);
            else
                return compareDay(otherEvent);
    }

    //METODI PER COMPARARE ANNI MESI E GIORNI:
    //anni
    private int compareYear(Event otherEvent){
        if(this.getYear() > otherEvent.getYear())
            return 1;
        else if(this.getYear() < otherEvent.getYear())
            return -1;
        else
            return 0;
    }

    //mesi
    private int compareMonth(Event otherEvent){
        if(this.getMonth() > otherEvent.getMonth())
            return 1;
        else if(this.getMonth() < otherEvent.getMonth())
            return -1;
        else
            return 0;
    }

    //Giorni-->Questo metodo ritorna 1 anche se l'evento è uguale altrimenti il treeset lo scarterebbe
    private int compareDay(Event otherEvent){
        if(this.getDay() >= otherEvent.getDay())
            return 1;
        else
            return -1;
    }
}
