import java.util.List;
import java.util.Set;

import util.Pair;


/********************************************************************
 * * Main class to run the AlphaMiner algorithm
 * * This class processes command line arguments to extract traces
 * * and then calls the AlphaMiner to mine the footprint o Petri net model.
 * * * Usage: "java Main task1,task2,task3 task4,task5 ..."
 * * * Each argument represents a list of traces separated by spaces, 
 * * * with tasks separated by commas.
 * * * Example: java Main A,C,D A,B,C,D A,B,D
 * (See EventLogTracesExamples.txt for more examples)
 *******************************************************************
 */
public class Main {

    public static void main(String[] args) {
        if (args.length <= 0) {
			System.out.println("No command line arguments found.");

		} else {
			String[][] traces = Args2Traces(args);
			String[][] footprint = AlphaMiner.mineFootprint(traces);

			System.out.println("Footprint:");
			for (String[] trace : footprint) {
				System.out.println(String.join(", ", trace));
			}

			//List<Pair<Set<String>, Set<String>>> places = AlphaMiner.footprint2Places(footprint);
			//System.out.println("Places:");
			//for (Pair<Set<String>, Set<String>> place : places) {
			//	System.out.println("Preset: " + place.first + " -> Postset: " + place.second);
			//}

			//Set<Pair<Set<String>,Set<String>>> places = AlphaMiner.minePetriNet(traces);
			//System.out.println("Places:");
			//for (Pair<Set<String>, Set<String>> place : places) {
			//	System.out.println("Preset: " + place.first + " -> Postset: " + place.second);
			//}
			
		}
    }

	public static String[][] Args2Traces(String[] args) {
		String[][] resultado = new String[args.length][];
        for (int i = 0; i < args.length; i++) {
            resultado[i] = args[i].trim().split(",");
        }
        return resultado;
	}

	
}
