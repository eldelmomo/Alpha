import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import util.Pair;

public class AlphaMiner {

    AlphaMiner() {
        // Default constructor
    }

    public static Set<Pair<Set<String>,Set<String>>> minePetriNet(String[][] traces) {

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

        printSet(causal);
        printSet(parallels);
        printSet(independent);

        // Step 5, 6 and 7:
        List<Pair<List<String>, List<String>>> places = new ArrayList<>();

        Set<Pair<String, String>> causalPreset = new HashSet<>(causal);
        Set<Pair<String, String>> causalPostset = new HashSet<>(causal);
        for (String a : tasks) {

            // Find all b such that a -> b : Postset
            List<String> post = new ArrayList<>();
            for (String b : tasks) {
                if (causalPostset.contains(new Pair<>(a, b))) {
                    post.add(b);
                }
            }
            if (post.isEmpty()) {
                continue;
            } else if (post.size() == 1) { // a -> b
                String b = post.get(0);
                places.add(new Pair<>(Arrays.asList(a), Arrays.asList(b)));
            } else {
                for (int i = 0; i < post.size(); i++) {
                    for (int j = 0; j < post.size(); j++) {
                        if (parallels.contains(new Pair<>(post.get(i), post.get(j)))) {
                            // a -> b , a -> c , b || c
                            places.add(new Pair<>(Arrays.asList(a), Arrays.asList(post.get(i))));
                            places.add(new Pair<>(Arrays.asList(a), Arrays.asList(post.get(j))));
                        } else {
                            // a -> b , a -> c , b # c
                            // a -> b , a -> c , b -> c 
                            places.add(new Pair<>(Arrays.asList(a), Arrays.asList(post.get(i), post.get(j))));
                        }
                    }
                }
            }
            for (String b : post) {
                causalPostset.remove(new Pair<>(a,b)); // Remove processed pairs
            }

            // Find all b such that b -> a : Preset
            List<String> pre = new ArrayList<>();
            for (String b : tasks) {
                if (causalPreset.contains(new Pair<>(b, a))) {
                    pre.add(b);
                }
            }
            if (pre.isEmpty()) {
                continue;
            } else {
                for (int i = 0; i < pre.size(); i++) {
                    for (int j = 0; j < pre.size(); j++) {
                        if (parallels.contains(new Pair<>(pre.get(i), pre.get(j)))) {
                            // a -> c , b -> c , a || b
                            places.add(new Pair<>(Arrays.asList(pre.get(i)), Arrays.asList(a)));
                            places.add(new Pair<>(Arrays.asList(pre.get(j)), Arrays.asList(a)));
                        } else {
                            // a -> c , b -> c , a # b
                            // a -> c , b -> c , a -> b
                            places.add(new Pair<>(Arrays.asList(pre.get(i), pre.get(j)), Arrays.asList(a)));
                        }
                    }
                }
            }
            for (String b : pre) {
                causalPreset.remove(new Pair<>(b,a)); // Remove processed pairs
            }
        }

        // Convert to Set<Pair<Set<String>, Set<String>>>
        Set<Pair<Set<String>, Set<String>>> placesSet = new HashSet<>();
        for (Pair<List<String>, List<String>> place : places) {
            Set<String> preset = new HashSet<>(place.first);
            Set<String> postset = new HashSet<>(place.second);
            placesSet.add(new Pair<>(preset, postset));
        }
        return placesSet;
    }

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

    public static void printSet(Set<Pair<String, String>> causal) {
        for (Pair<String, String> pair : causal) {
            System.out.print(pair);
        }
        System.out.println();
    }

    public static List<Pair<Set<String>, Set<String>>> footprint2Places(String[][] matrix) {
    List<Pair<Set<String>, Set<String>>> places = new ArrayList<>();
    int n = matrix.length - 1;
    String[] tasks = new String[n];
    for (int i = 0; i < n; i++) {
        tasks[i] = matrix[0][i + 1];
    }

    // Buscar todos los pares (A,B) donde todas las relaciones de A a B son ">"
    for (int i = 0; i < n; i++) {
        for (int j = 0; j < n; j++) {
            if (i == j) continue;
            if (">".equals(matrix[i + 1][j + 1])) {
                Set<String> preset = new HashSet<>();
                Set<String> postset = new HashSet<>();
                preset.add(tasks[i]);
                postset.add(tasks[j]);
                places.add(new Pair<>(preset, postset));
            }
        }
    }
    return places;
}

}
