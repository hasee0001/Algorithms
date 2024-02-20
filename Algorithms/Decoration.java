package Algorithms;
public class Decoration {

    public static int findMinimumCost(int[][] costs) {
        if (costs == null || costs.length == 0 || costs[0].length == 0) {
            return 0; // Array checking if empty or null 
        }
        
        int n = costs.length; // No.venues
        int k = costs[0].length; // No. given themes
        
        //array which stores current and previous cost
        int[] prevRow = new int[k];
        int[] currRow = new int[k];
        
        System.arraycopy(costs[0], 0, prevRow, 0, k);
        
        for (int i = 1; i < n; i++) {
            int minCost = Integer.MAX_VALUE;
            int secondMinCost = Integer.MAX_VALUE;
            for (int cost : prevRow) {
                if (cost < minCost) {
                    secondMinCost = minCost;
                    minCost = cost;
                } else if (cost < secondMinCost) {
                    secondMinCost = cost;
                }
            }
            
            for (int j = 0; j < k; j++) {
                currRow[j] = costs[i][j] + (prevRow[j] == minCost ? secondMinCost : minCost);
            }
            
            System.arraycopy(currRow, 0, prevRow, 0, k);
        }
        
        int minCost = Integer.MAX_VALUE;
        for (int cost : prevRow) {
            minCost = Math.min(minCost, cost);
        }
        
        return minCost;
    }

    public static void main(String[] args) {
        int[][] costs = {{1, 3, 2}, {4, 6, 8}, {3, 1, 5}};
        int minCost = findMinimumCost(costs);
        System.out.println("Minimum cost to decorate all venues: " + minCost);
    }
}
