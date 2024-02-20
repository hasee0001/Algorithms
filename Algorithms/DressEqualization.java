package Algorithms;
public class DressEqualization {

    // Method to calculate the minimum moves required to equalize dress counts
    public static int minMovesToEqualize(int[] machines) {
        int totalDresses = calculateTotalDresses(machines);
        int numMachines = machines.length;

        if (totalDresses % numMachines != 0) {
            return -1;
        }

        int targetDresses = totalDresses / numMachines;
        int moves = 0;
        int dressesLeft = 0;

        // Calculate excess dresses & maximum moves
        for (int dresses : machines) {
            dressesLeft += dresses - targetDresses;
            moves = Math.max(moves, Math.abs(dressesLeft));
        }

        return moves;
    }

    // Method to calculate the total number of dresses in all machines
    private static int calculateTotalDresses(int[] machines) {
        int totalDresses = 0;
        for (int dresses : machines) {
            totalDresses += dresses;
        }
        return totalDresses;
    }

    public static void main(String[] args) {
        int[] machines = {1, 0, 5};
        System.out.println("Minimum moves required: " + minMovesToEqualize(machines));
    }
}