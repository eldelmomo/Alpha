package util;

import java.util.Set;

public class Print {
    /**
     * Prints a set of elements in a formatted way.
     * @param set The set to print.
     * @param <T> The type of elements in the set.
     */
    public static <T> void printSet(Set<T> set) {
        if (set.isEmpty()) {
            System.out.println("Set is empty.");
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("{");
            for (T element : set) {
                sb.append(element.toString()).append(",");
            }
            sb.setLength(sb.length() - 1); 
            sb.append("}");
            System.out.println(sb.toString());
        }
    }
}
