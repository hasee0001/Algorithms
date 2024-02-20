package Algorithms;
import java.util.Arrays;
import java.util.PriorityQueue;

public class EngineBuild {

    public static int minimumTime(int[] engines, int splitCost) {
        int n = engines.length;
        
        // Sort the engines in descending order 
        Arrays.sort(engines);
        
        // time taken by each engineer
        PriorityQueue<Integer> pq = new PriorityQueue<>();
        
        pq.offer(0);
        
        // Iterate through each engine
        for (int i = n - 1; i >= 0; i--) {
            int minTime = pq.poll();
            int timeToBuild = Math.max(minTime, engines[i]) + splitCost;
            pq.offer(timeToBuild);
        }
        return pq.poll();
    }

    public static void main(String[] args) {
        int[] engines = {1, 2, 3};
        int splitCost = 1;
        
        int minTime = minimumTime(engines, splitCost);
        System.out.println("Minimum time to build all engines: " + minTime);
    }
}
