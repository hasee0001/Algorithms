package Algorithms;
import java.util.*;

public class NetworkDeviceImpacted {
    
    public static List<Integer> findImpactedDevices(int[][] edges, int targetDevice) {
        Map<Integer, List<Integer>> graph = buildGraph(edges);
        Set<Integer> visited = new HashSet<>();
        List<Integer> impactedDevices = new ArrayList<>();
        
        dfs(graph, targetDevice, visited, impactedDevices);
        
        return impactedDevices;
    }
    
    private static Map<Integer, List<Integer>> buildGraph(int[][] edges) {
        Map<Integer, List<Integer>> graph = new HashMap<>();
        
        for (int[] edge : edges) {
            int from = edge[0];
            int to = edge[1];
            
            graph.putIfAbsent(from, new ArrayList<>());
            graph.putIfAbsent(to, new ArrayList<>());
            
            graph.get(from).add(to);
            graph.get(to).add(from); // Assuming bidirectional connections
        }
        
        return graph;
    }
    
    private static void dfs(Map<Integer, List<Integer>> graph, int device, Set<Integer> visited, List<Integer> impactedDevices) {
        visited.add(device);
        
        for (int neighbor : graph.getOrDefault(device, new ArrayList<>())) {
            if (!visited.contains(neighbor)) {
                impactedDevices.add(neighbor);
                dfs(graph, neighbor, visited, impactedDevices);
            }
        }
    }
    
    public static void main(String[] args) {
        int[][] edges = {
            {0, 1}, {0, 2}, {1, 3}, {1, 6}, {2, 4}, {4, 6}, {4, 5}, {5, 7}
        };
        int targetDevice = 4;
        
        List<Integer> impactedDevices = findImpactedDevices(edges, targetDevice);
        System.out.println("Impacted Device List: " + impactedDevices); // Output: [5, 7]
    }
}
