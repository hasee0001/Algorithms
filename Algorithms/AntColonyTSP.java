package Algorithms;
import java.util.ArrayList;
import java.util.List;

public class AntColonyTSP {
    
    private int[][] distanceMatrix;
    private int numCities;
    
    public AntColonyTSP(int[][] distanceMatrix) {
        this.distanceMatrix = distanceMatrix;
        this.numCities = distanceMatrix.length;
    }
    
    public List<Integer> solveTSP() {
        List<Integer> tour = new ArrayList<>();
        boolean[] visited = new boolean[numCities];
        
        // Start from city 0
        int currentCity = 0;
        tour.add(currentCity);
        visited[currentCity] = true;
        
        for (int i = 0; i < numCities - 1; i++) {
            int nextCity = chooseNextCity(currentCity, visited);
            tour.add(nextCity);
            visited[nextCity] = true;
            currentCity = nextCity;
        }
        
        // Return to the starting city to complete the tour
        tour.add(0);
        
        return tour;
    }
    
    private int chooseNextCity(int currentCity, boolean[] visited) {
        int nextCity = -1;
        int shortestDistance = Integer.MAX_VALUE;
        
        for (int i = 0; i < numCities; i++) {
            if (!visited[i] && i != currentCity && distanceMatrix[currentCity][i] < shortestDistance) {
                shortestDistance = distanceMatrix[currentCity][i];
                nextCity = i;
            }
        }
        
        return nextCity;
    }
    
    // Example usage
    public static void main(String[] args) {
        // Example distance matrix (replace with actual data)
        int[][] distanceMatrix = {
            {0, 10, 15, 20},
            {10, 0, 35, 25},
            {15, 35, 0, 30},
            {20, 25, 30, 0}
        };
        
        AntColonyTSP antColonyTSP = new AntColonyTSP(distanceMatrix);
        List<Integer> tour = antColonyTSP.solveTSP();
        
        System.out.println("Ant Colony TSP Tour:");
        for (int city : tour) {
            System.out.print(city + " ");
        }
        System.out.println();
    }
}