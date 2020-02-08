package Soluzione;

import java.util.*;

/**
*Questa classe ha lo scopo di salvare tutti i dati riguardanti le mappe         emozionali
*/
public class DataManager {
	public HashMap<Integer, TreeSet<Event>> dataCollector;

	//Crea un nuovo hasmap di treeset e gli inserisce all'interno le date da 2000 a 2030
	public DataManager() {
		this.dataCollector = new HashMap<>();
		for (int i = 2000; i <= 2030; i++)
			this.dataCollector.put(i, new TreeSet<>(Event::compareTo));
	}

	//Aggiunge eventi al TreeSet. O(log(m)) dove m è il numero di dati di un anno
	public void addEvent(Event event) {
		try{
			this.dataCollector.get(event.getYear()).add(event);
		}
		catch (NullPointerException exc){
			System.err.println("Year needed not in store: it will be added");
			dataCollector.put(event.getYear(),new TreeSet<>(Event::compareTo));
		}
	}

	/*Cerca se gli eventi sono presenti nella struttura dati: costo = O(1) + O(1) = O(1)
	* Ritorna false se la grandezza del treeset è 0 o se ottiene null pointer exception quando cerca il treeset di un anno
	* */
	private boolean areThereEvents(Event lowerBound, Event upperBound) {
		try{
			if(this.dataCollector.get(lowerBound.getYear()).size() == 0)
				return false;

			if(this.dataCollector.get(upperBound.getYear()).size() == 0)
				return false;
		}
		catch (NullPointerException exc){
			return false;
		}
		return true;
	}

	/**
    *Crea la mappa di eventi se ci sono date con eventi
	 */
	public void createMap(String dateValue){

		long startTime = System.currentTimeMillis();

		//StringParser ritorna un hashmap contenente il primo evento da considerare e l'ultimo
		HashMap<String, Event> bounds = StringParser.parseStringToUpperLowerDate(dateValue);
		Event lowerBoundEvt = bounds.get("LOWERBOUND");
		Event upperBoundEvt = bounds.get("UPPERBOUND");

		/*Array di hashmap usato per contare l'ammontare di stati emozionali accaduti in ogni POI:
			POI1--> emotionalData[0] POI2---> emotionalData[1] POI3---> emotionalData[2]

		  Array di interi usato per contare gli eventi totali in modo da non doverli calcolare successivamente:
		  	Evita overhead di operazioni

		 */

		if (areThereEvents(lowerBoundEvt, upperBoundEvt)) {

			HashMap<EmotionalStates, Integer>[] emotionalData = new HashMap[3];
			for (int i = 0; i < 3; i++)
				emotionalData[i] = new HashMap<>();
			int[] totalEvents = new int[3];

			//Inizializzo array di hash map e hasmap con tutti gli stati a zero occorrenze

			EmotionalStates[] emotionalStates = EmotionalStates.toArray();

			for (HashMap<EmotionalStates, Integer> hashMap : emotionalData) {
				for (EmotionalStates states : emotionalStates) {
					hashMap.put(states, 0);
				}
			}

			//creo l' array di risultati

			if (lowerBoundEvt.getYear() == upperBoundEvt.getYear()) {
				mapEventsWithSameYear(lowerBoundEvt, upperBoundEvt, emotionalData, totalEvents);
			} else if (lowerBoundEvt.getYear() < upperBoundEvt.getYear()) {
				mapEventsWithDifferentYears(lowerBoundEvt, upperBoundEvt, emotionalData, totalEvents);
			} else
				System.err.println("Non è possibile mappare dati da una data di valore maggiore ad una di minore ");


			System.out.println("total events di POI1: " + totalEvents[0]);
			System.out.println("total events di POI2: " + totalEvents[1]);
			System.out.println("total events di POI3: " + totalEvents[2] + "\n");

			/*CREO PERCENTUALI: trovo il 100% degli eventi e lo divido per il valore dello stato emozionale:
			//Se divido 100 per il valore trovato avrò la percentuale*/

			Double arrabbiato, felice, sorpreso, triste, neutro;
			
			for(int i = 1; i <= 3; i++){

				//ARRABBIATO
				arrabbiato = (double)totalEvents[i-1]/(double)emotionalData[i-1].get(EmotionalStates.ARRABBIATO);

				//FELICE
				felice = (double)totalEvents[i-1]/(double)emotionalData[i-1].get(EmotionalStates.FELICE);

				//SORPRESO
				sorpreso = (double)totalEvents[i-1]/(double)emotionalData[i-1].get(EmotionalStates.SORPRESO);

				//TRSITE
				triste = (double)totalEvents[i-1]/(double)emotionalData[i-1].get(EmotionalStates.TRISTE);

				//NEUTRO
				neutro = (double)totalEvents[i-1]/(double)emotionalData[i-1].get(EmotionalStates.NEUTRO);

				String result = "POI"+ i + " ---> ";

				result += arrabbiato.equals(Double.NaN) ?   0 + "% A " : Math.round(100/arrabbiato) + "% A " ;

				result += felice.equals(Double.NaN)     ?   0 + "% F " : Math.round(100/felice) + "% F " ;

				result += sorpreso.equals(Double.NaN)   ?   0 + "% S " : Math.round(100/sorpreso) + "% S ";

				result += triste.equals(Double.NaN)     ?   0 + "% T " : Math.round(100/triste) + "% T ";

				result += neutro.equals(Double.NaN)     ?   0 + "% N " : Math.round(100/neutro) + "% N " ;


				System.out.println(result);
			}

			System.out.println();

			long endTime = System.currentTimeMillis();

			System.err.println("start time: " + startTime);
			System.err.println("end time: " + endTime);
			System.err.println("Tempo totale (in millisecondi) di creazione mappa: " + (endTime - startTime));
			System.err.println("Tempo totale (in secondi) di creazione mappa: " + (endTime - startTime)/1000);
		}
		else
			System.err.println("Non è possibile creare mappe con date senza eventi \n");

	}

	/**
	Crea la mappa di eventi con lo stesso anno iterando sul TreeSet di *quell'anno
	*/
	private void mapEventsWithSameYear(Event lowerBoundEvt, Event upperBoundEvt,
										HashMap<EmotionalStates, Integer>[] emotionalData, int[] toalEvents) {

		TreeSet<Event> eventContainer = this.dataCollector.get(lowerBoundEvt.getYear());
		Iterator<Event> iterator = eventContainer.iterator();
		Event iterEvent;

		while (iterator.hasNext()) {

			iterEvent = iterator.next();

			if (iterEvent.compareTo(upperBoundEvt) == 1 && iterEvent.getDay() > upperBoundEvt.getDay())
				break;

			if (iterEvent.compareTo(lowerBoundEvt) == -1)
				continue;

			switch (iterEvent.getPoi()) {

				case "POI1":
					emotionalData[0].put(iterEvent.getEmotionalState(), emotionalData[0].get(iterEvent.getEmotionalState()) + 1);
					toalEvents[0]++;
					break;

				case "POI2":
					emotionalData[1].put(iterEvent.getEmotionalState(), emotionalData[0].get(iterEvent.getEmotionalState()) + 1);
					toalEvents[1]++;
					break;

				case "POI3":
					emotionalData[2].put(iterEvent.getEmotionalState(), emotionalData[0].get(iterEvent.getEmotionalState()) + 1);
					toalEvents[2]++;
					break;

				default:
					break;
			}
		}

	}


	/**
	*Crea la mappa di eventi cdi diverso anno iterando sul TreeSet degli anni *compresi
	*/
	
	private void mapEventsWithDifferentYears(Event lowerBoundEvt, Event upperBoundEvt,
											 HashMap<EmotionalStates, Integer>[] emotionalData, int[] toalEvents) {


		/*Creo un Iterator e itero sugli anni di interesse: costo O(n) per ogni anno iterato con n numero dei dati*/

		TreeSet<Event> current; //servirà da puntatore di treeset quando si itera sulla struttura dati
		Iterator<Event> iterator;
		Event iterEvent;

		for(int i = lowerBoundEvt.getYear(); i <= upperBoundEvt.getYear(); i++) {

			current = this.dataCollector.get(i);// treeset di eventi corrente
			iterator =  current.iterator();

			while (iterator.hasNext()) {

				iterEvent = iterator.next();

				if (iterEvent.compareTo(upperBoundEvt) == 1 && iterEvent.getDay() > upperBoundEvt.getDay())
					break;

				if (i == lowerBoundEvt.getYear() && iterEvent.compareTo(lowerBoundEvt) == -1)
					continue;

				switch (iterEvent.getPoi()) {

					case "POI1":
						emotionalData[0].put(iterEvent.getEmotionalState(), emotionalData[0].get(iterEvent.getEmotionalState()) + 1);
						toalEvents[0]++;
						break;

					case "POI2":
						emotionalData[1].put(iterEvent.getEmotionalState(), emotionalData[1].get(iterEvent.getEmotionalState()) + 1);
						toalEvents[1]++;
						break;

					case "POI3":
						emotionalData[2].put(iterEvent.getEmotionalState(), emotionalData[2].get(iterEvent.getEmotionalState()) + 1);
						toalEvents[2]++;
						break;

					default:
						break;
				}
			}
		}
	}
}
