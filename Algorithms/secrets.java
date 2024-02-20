package Algorithms;
import java.util.*;

public class secrets {
    
    // Method to find individuals who know secrets within specified intervals
    public static List<Integer> findKnownSecret(int n, int[][] intervals, int firstPerson) {
        // graph to show relation between individuals
        List<List<Integer>> graph = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            graph.add(new ArrayList<>());
        }
        // Populate graph based on provided intervals
        for (int[] interval : intervals) {
            int start = interval[0];
            int end = interval[1];
            for (int i = start; i <= end; i++) {
                if (i != firstPerson) {
                    graph.get(firstPerson).add(i);
                }
            }
        }
        
        Set<Integer> known = new HashSet<>();
        // Perform depth-first search 
        dfs(graph, firstPerson, known);
        
        List<Integer> result = new ArrayList<>(known);
        Collections.sort(result);
        return result;
    }
    
    // Used depth-first search
    private static void dfs(List<List<Integer>> graph, int person, Set<Integer> known) {
        known.add(person);
        // Traverse neighbors
        for (int neighbor : graph.get(person)) {
            if (!known.contains(neighbor)) {
                dfs(graph, neighbor, known);
            }
        }
    }

    
    public static void main(String[] args) {
        int n = 6;
        int[][] intervals = {{0, 1}, {2, 3}, {4, 5}};
        int firstPerson = 1;

        List<Integer> known = findKnownSecret(n, intervals, firstPerson);
      
        System.out.println("Individuals who know secrets: " + known);
    }
}
