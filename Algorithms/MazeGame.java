package Algorithms;
import java.util.*;

class MazeGame {
    public int shortestPathAllKeys(String[] grid) {
        int m = grid.length;
        int n = grid[0].length();
        int totalKeys = 0;
        int[] start = new int[2];
        
        // Find total keys and starting position
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (grid[i].charAt(j) == 'S') {
                    start[0] = i;
                    start[1] = j;
                }
                if (Character.isLowerCase(grid[i].charAt(j))) {
                    totalKeys++;
                }
            }
        }
        
        // Initialize visited set and queue for BFS
        Set<String> visited = new HashSet<>();
        Queue<int[]> queue = new LinkedList<>();
        queue.offer(new int[]{start[0], start[1], 0, 0});
        visited.add(start[0] + "," + start[1] + ",0");
        
        // Define directions: up, down, left, right
        int[][] dirs = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        
        // Start BFS
        while (!queue.isEmpty()) {
            int[] curr = queue.poll();
            int x = curr[0];
            int y = curr[1];
            int keys = curr[2];
            int steps = curr[3];
            
            // Check if all keys are collected
            if (keys == (1 << totalKeys) - 1) {
                return steps;
            }
            
            // Explore neighbors
            for (int[] dir : dirs) {
                int newX = x + dir[0];
                int newY = y + dir[1];
                int newKeys = keys;
                
                if (newX >= 0 && newX < m && newY >= 0 && newY < n && grid[newX].charAt(newY) != '#') {
                    char c = grid[newX].charAt(newY);
                    if (Character.isLowerCase(c)) {
                        newKeys |= 1 << (c - 'a');
                    }
                    if (Character.isUpperCase(c) && ((keys >> (c - 'A')) & 1) == 0) {
                        continue; // Cannot proceed without key
                    }
                    String newState = newX + "," + newY + "," + newKeys;
                    if (!visited.contains(newState)) {
                        visited.add(newState);
                        queue.offer(new int[]{newX, newY, newKeys, steps + 1});
                    }
                }
            }
        }
        
        return -1; // Cannot collect all keys
    }
}
