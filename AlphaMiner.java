import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import util.Pair;
import util.Print;

/********************************************************************
 * * Main class to run the AlphaMiner algorithm
 * * This class processes command line arguments to extract traces
 * * and then calls the AlphaMiner to mine the footprint of a Petri net model.
 * * Usage: "java Main task1,task2,task3 task4,task5 ..."
 * * Each argument represents a list of traces separated by spaces,
 * * with tasks separated by commas.
 * * Example: java Main A,C,D A,B,C,D A,B,D
 * (See EventLogTracesExamples.txt for more examples)
 *******************************************************************
 */
public class AlphaMiner {

    AlphaMiner() {
        // Default constructor
    }

    /**
     * Mines the footprint of a Petri net model from the given traces.
     *
     * @param traces The event traces to mine the footprint from.
     * @return A 2D array representing the footprint of the Petri net.
     */
    public static Set<Pair<Set<String>, Set<String>>> minePetriNet(String[][] traces) {

        // Step 1, 2 and 3: Obtain unique events, initial events, and final events
        Set<String> tasks = new HashSet<>();
        Set<String> initials = new HashSet<>();
        Set<String> finals = new HashSet<>();
        for (String[] trace : traces) {
            tasks.addAll(Arrays.asList(trace));
            if (trace.length > 0) {
                initials.add(trace[0]);
                finals.add(trace[trace.length - 1]);
            }
        }

        // Step 4.1: Group order relationships
        Set<Pair<String, String>> directRelations = new HashSet<>();
        for (String[] trace : traces) {
            for (int i = 0; i < trace.length - 1; i++) {
                directRelations.add(new Pair<>(trace[i], trace[i + 1]));
            }
        }

        // Step 4.2: Obtains causal relationships (a -> b)
        Set<Pair<String, String>> causal = new HashSet<>();
        // Step 4.3: Obtains parallel relationships (a || b)
        Set<Pair<String, String>> parallels = new HashSet<>();
        // Step 4.4: Obtains independent relationships (a # b)
        Set<Pair<String, String>> independent = new HashSet<>();

        for (String a : tasks) {
            for (String b : tasks) {
                boolean ab = directRelations.contains(new Pair<>(a, b));
                boolean ba = directRelations.contains(new Pair<>(b, a));
                if (ab && !ba) {
                    causal.add(new Pair<>(a, b));
                } else if (ab && ba) {
                    parallels.add(new Pair<>(a, b));
                } else if (!ab && !ba) {
                    independent.add(new Pair<>(a, b));
                }
            }
        }

        System.out.println("\n[DEBUG] >> Causal, Parallel, Independent Relationships");
        Print.printSet(causal);
        Print.printSet(parallels);
        Print.printSet(independent);
        System.out.println("[DEBUG] >> Causal, Parallel, Independent Relationships\n");

        // Step 5, 6 and 7:
        Set<Pair<Set<String>, Set<String>>> places = new HashSet<>();

        Set<Pair<String, String>> causalPost = new HashSet<>(causal);
        Set<Pair<String, String>> causalPre = new HashSet<>(causal);

        // Add initials and finals as places
        places.add(new Pair<>(new HashSet<>(Set.of("")), initials));
        places.add(new Pair<>(finals, new HashSet<>(Set.of(""))));

        for (String a : tasks) {

            // Find all b such that a -> b : Postset
            List<String> post = new ArrayList<>();
            for (String b : tasks) {
                if (causalPost.contains(new Pair<>(a, b))) {
                    post.add(b);
                }
            }
            if (post.size() == 1) { // a -> b
                String b = post.get(0);
                places.add(new Pair<>(new HashSet<>(Set.of(a)), new HashSet<>(Set.of(b))));
            } else if (!post.isEmpty()) {
                for (int i = 0; i < post.size(); i++) {
                    for (int j = 0; j < post.size(); j++) {
                        if (i == j)
                            continue;
                        if (parallels.contains(new Pair<>(post.get(i), post.get(j)))) {
                            // a -> b , a -> c , b || c
                            places.add(new Pair<>(new HashSet<>(Set.of(a)), new HashSet<>(Set.of(post.get(i)))));
                            places.add(new Pair<>(new HashSet<>(Set.of(a)), new HashSet<>(Set.of(post.get(j)))));
                        } else {
                            // a -> b , a -> c , b # c
                            // a -> b , a -> c , b -> c
                            places.add(new Pair<>(new HashSet<>(Set.of(a)),
                                    new HashSet<>(Set.of(post.get(i), post.get(j)))));

                        }
                    }
                }
            }
            for (String b : post) {
                causalPost.remove(new Pair<>(a, b)); // Remove processed pairs
            }

            // Find all b such that b -> a : Preset
            List<String> pre = new ArrayList<>();
            for (String b : tasks) {
                if (causalPre.contains(new Pair<>(b, a))) {
                    pre.add(b);
                }
            }
            if (!pre.isEmpty()) {
                for (int i = 0; i < pre.size(); i++) {
                    for (int j = 0; j < pre.size(); j++) {
                        if (i == j)
                            continue;
                        if (parallels.contains(new Pair<>(pre.get(i), pre.get(j)))) {
                            // a -> c , b -> c , a || b
                            places.add(new Pair<>(new HashSet<>(Set.of(pre.get(i))), new HashSet<>(Set.of(a))));
                            places.add(new Pair<>(new HashSet<>(Set.of(pre.get(j))), new HashSet<>(Set.of(a))));
                        } else {
                            // a -> c , b -> c , a # b
                            // a -> c , b -> c , a -> b
                            places.add(new Pair<>(new HashSet<>(Set.of(pre.get(i), pre.get(j))),
                                    new HashSet<>(Set.of(a))));
                        }
                    }
                }
            }
            for (String b : pre) {
                causalPre.remove(new Pair<>(b, a));
            }
        }

        // Remove redundant places (remove subsets)
         places.removeIf(p1 -> places.stream().anyMatch(p2 -> !p1.equals(p2) &&
         p2.first.containsAll(p1.first) && p2.second.containsAll(p1.second)));

        return places;
    }

    /**
     * Mines the footprint of a Petri net model from the given traces.
     *
     * @param traces The event traces to mine the footprint from.
     * @return A 2D array representing the footprint of the Petri net.
     */
    public static String[][] mineFootprint(String[][] traces) {

        // Step 1, 2 and 3: Obtain unique events, initial events, and final events
        Set<String> tasks = new HashSet<>();
        Set<String> initials = new HashSet<>();
        Set<String> finals = new HashSet<>();
        for (String[] trace : traces) {
            tasks.addAll(Arrays.asList(trace));
            if (trace.length > 0) {
                initials.add(trace[0]);
                finals.add(trace[trace.length - 1]);
            }
        }

        // Step 4.1: Group order relationships
        Set<Pair<String, String>> directRelations = new HashSet<>();
        for (String[] trace : traces) {
            for (int i = 0; i < trace.length - 1; i++) {
                directRelations.add(new Pair<>(trace[i], trace[i + 1]));
            }
        }

        // Step 4.2: Obtains causal relationships (a -> b)
        Set<Pair<String, String>> causal = new HashSet<>();
        // Step 4.3: Obtains parallel relationships (a || b)
        Set<Pair<String, String>> parallels = new HashSet<>();
        // Step 4.4: Obtains independent relationships (a # b)
        Set<Pair<String, String>> independent = new HashSet<>();

        for (String a : tasks) {
            for (String b : tasks) {
                boolean ab = directRelations.contains(new Pair<>(a, b));
                boolean ba = directRelations.contains(new Pair<>(b, a));
                if (ab && !ba) {
                    causal.add(new Pair<>(a, b));
                } else if (ab && ba) {
                    parallels.add(new Pair<>(a, b));
                } else if (!ab && !ba) {
                    independent.add(new Pair<>(a, b));
                }
            }
        }

        // Step 5: Create the footprint
        int n = tasks.size();
        String[] taskArray = tasks.toArray(new String[0]);
        String[][] matrix = new String[n + 1][n + 1];

        // First row and first column with task names
        matrix[0][0] = "_";
        for (int i = 0; i < n; i++) {
            matrix[0][i + 1] = taskArray[i];
            matrix[i + 1][0] = taskArray[i];
        }

        // Fill the footprint matrix
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (causal.contains(new Pair<>(taskArray[i], taskArray[j]))) {
                    matrix[i + 1][j + 1] = ">";
                    matrix[j + 1][i + 1] = "<";
                } else if (parallels.contains(new Pair<>(taskArray[i], taskArray[j]))) {
                    matrix[i + 1][j + 1] = "=";
                } else if (independent.contains(new Pair<>(taskArray[i], taskArray[j]))) {
                    matrix[i + 1][j + 1] = "#";
                }
            }
        }

        return matrix;
    }

}
