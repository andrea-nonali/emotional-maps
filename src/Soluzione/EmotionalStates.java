package Soluzione;

/**
*Classe enum per rappresentare in modo immutabile gli stati
*/

public enum EmotionalStates {

	ARRABBIATO,
	FELICE,
	SORPRESO,
	TRISTE,
	NEUTRO;

	/**
	*converte una stringa in uno stato emozionale di questa classe
	*/
	
	public static EmotionalStates convertToEmotionalState(String emotionalState){

		EmotionalStates em = null;

		switch (emotionalState) {
			case "A":
				em = EmotionalStates.ARRABBIATO;
				break;
			case "F" :
				em = EmotionalStates.FELICE;
				break;
			case "S" :
				em = EmotionalStates.SORPRESO;
				break;
			case "T" :
				em = EmotionalStates.TRISTE;
				break;
			case "N" :
				em = EmotionalStates.NEUTRO;
		}
		return em;
	}

	/**
	*ritorna un array con tutti gli stati emozionali
	*/
	
	public static EmotionalStates[] toArray() {
		return new EmotionalStates[]{EmotionalStates.ARRABBIATO, EmotionalStates.FELICE,
				EmotionalStates.SORPRESO, EmotionalStates.TRISTE, EmotionalStates.NEUTRO};
	}
}
