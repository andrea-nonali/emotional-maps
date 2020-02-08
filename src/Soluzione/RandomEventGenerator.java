package Soluzione;

import java.io.BufferedWriter;
import java.io.FileWriter;

/**
*Genera eventi random e li salva nel percorso utile
*/

public class RandomEventGenerator {
	private static final String ALPHA_NUMERIC_STRING = "abcdefghijklmnopqrstuvwxyz0123456789";

	public static String userId(){
		StringBuilder builder = new StringBuilder();
		for(int i = 0; i< 5;i++) {
			int character = (int) (Math.random() * ALPHA_NUMERIC_STRING.length());
			builder.append(ALPHA_NUMERIC_STRING.charAt(character));
		}
		builder.append( " ");
		return builder.toString();
	}

	public static String login() {
		if(Math.round(Math.random() * 100) > 50)
			return "LOGIN ";
		return "LOGOUT ";

	}

	public static String in () {
		if(Math.round(Math.random() * 100) > 50)
			return "OUT ";
		return "IN ";
	}

	public static  String POI() {

		long i = Math.round(Math.random() * 100);

		if(i > 30 && i < 66)
			return "45.473,9.173 ";
		if(i < 30)
			return "45.464,9.190 ";
		if (i > 66)
			return "45.458,9.181 ";

		return "45.458,9.181 ";
	}

	public static String EmotionalS() {
		double randomDouble = Math.random();
		randomDouble = randomDouble * 50 + 1;
		int randomInt = (int) randomDouble;

		if(randomInt <= 10 && randomInt >= 0)
			return "A";
		if(randomInt <= 20 && randomInt > 10)
			return "F";
		if(randomInt <= 30 && randomInt > 20)
			return "T";
		if(randomInt <= 40 && randomInt > 30)
			return "N";
		if(randomInt <= 50 && randomInt > 40)
			return "S";

		return "S";
	}

	public static String date () {
		String date = "";
		double randomDouble = Math.random();
		randomDouble = randomDouble * 20 + 10;
		int randomInt = (int) randomDouble;
		date += Integer.toString(randomInt);

		randomDouble = Math.random();
		randomDouble = randomDouble * 3 + 10;
		randomInt = (int) randomDouble;
		date += Integer.toString(randomInt);

		randomDouble = Math.random();
		randomDouble = randomDouble * 30 + 2000;
		randomInt = (int) randomDouble;
		date += Integer.toString(randomInt);
		date += " ";
		return date;
	}

	public static void main(String[] args) {


		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter("/home/andrea/Documenti/Corsi_Università/Primo_Anno/LaboratorioA/test.txt"));
			for (int i = 0; i < 100000; i++) {
				String result = "";
				result += in();
				result += login();
				result += date();
				result += userId();
				result += POI();
				result += EmotionalS();

				writer.write(result + "\n");
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				writer.close();
			} catch (Exception e) {
			}
		}
	}
}
