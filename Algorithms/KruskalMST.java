package Algorithms;
import java.util.*;

// Class to represent an edge in the graph
class Edge implements Comparable<Edge> {
    int src, dest, weight;

    public Edge(int src, int dest, int weight) {
        this.src = src;
        this.dest = dest;
        this.weight = weight;
    }

    // Compare edges based on weight for sorting
    @Override
    public int compareTo(Edge other) {
        return this.weight - other.weight;
    }
}

// Class for implementing Disjoint Set data structure
class DisjointSet {
    int[] parent, rank;

    // Constructor to initialize the disjoint set
    public DisjointSet(int n) {
        parent = new int[n];
        rank = new int[n];
        for (int i = 0; i < n; i++) {
            parent[i] = i; // Each node is initially its own parent
            rank[i] = 0;   // Rank of each node is initially 0
        }
    }

    // Find operation with path compression
    public int find(int x) {
        if (parent[x] != x) {
            parent[x] = find(parent[x]); // Path compression
        }
        return parent[x];
    }

    // Union operation based on rank
    public void union(int x, int y) {
        int xRoot = find(x);
        int yRoot = find(y);

        if (xRoot == yRoot) return;

        if (rank[xRoot] < rank[yRoot]) {
            parent[xRoot] = yRoot;
        } else if (rank[xRoot] > rank[yRoot]) {
            parent[yRoot] = xRoot;
        } else {
            parent[yRoot] = xRoot;
            rank[xRoot]++;
        }
    }
}

// Main class containing Kruskal's algorithm
public class KruskalMST {
    // Function to find the minimum spanning tree using Kruskal's algorithm
    public static List<Edge> kruskalMST(List<Edge> edges, int V) {
        List<Edge> result = new ArrayList<>();
        DisjointSet ds = new DisjointSet(V);

        Collections.sort(edges); // Sort edges based on weight

        // Iterate through sorted edges
        for (Edge edge : edges) {
            int srcParent = ds.find(edge.src);
            int destParent = ds.find(edge.dest);

            // If including this edge doesn't form a cycle, add it to the result
            if (srcParent != destParent) {
                result.add(edge);
                ds.union(srcParent, destParent);
            }
        }

        return result; // Return the list of edges in the MST
    }

    // Main method to demonstrate the usage of Kruskal's algorithm
    public static void main(String[] args) {
        List<Edge> edges = new ArrayList<>();
        edges.add(new Edge(0, 1, 10));
        edges.add(new Edge(0, 2, 6));
        edges.add(new Edge(0, 3, 5));
        edges.add(new Edge(1, 3, 15));
        edges.add(new Edge(2, 3, 4));

        int V = 4; // Number of vertices in the graph

        // Call the kruskalMST function to find the MST
        List<Edge> mst = kruskalMST(edges, V);

        // Print the edges in the MST
        System.out.println("Edges in the MST:");
        for (Edge edge : mst) {
            System.out.println(edge.src + " - " + edge.dest + ": " + edge.weight);
        }
    }
}
