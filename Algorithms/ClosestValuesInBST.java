package Algorithms;
import java.util.*;

class TreeNode {
    int val;
    TreeNode left, right;

    TreeNode(int val) {
        this.val = val;
    }
}

public class ClosestValuesInBST {
    public List<Integer> closestValues(TreeNode root, double target, int x) {
        List<Integer> result = new ArrayList<>();
        PriorityQueue<Map.Entry<Double, Integer>> pq = new PriorityQueue<>(x,
                Comparator.comparingDouble(Map.Entry::getKey));

        // In-order traversal using stack
        Stack<TreeNode> stack = new Stack<>();
        TreeNode current = root;
        while (current != null || !stack.isEmpty()) {
            while (current != null) {
                stack.push(current);
                current = current.left;
            }
            current = stack.pop();
            double diff = Math.abs(current.val - target);
            if (pq.size() < x) {
                pq.offer(new AbstractMap.SimpleEntry<>(diff, current.val));
            } else {
                if (pq.peek().getKey() > diff) {
                    pq.poll();
                    pq.offer(new AbstractMap.SimpleEntry<>(diff, current.val));
                } else {
                    break;
                }
            }
            current = current.right;
        }
        while (!pq.isEmpty()) {
            result.add(pq.poll().getValue());
        }

        return result;
    }

    public static void main(String[] args) {
        TreeNode root = new TreeNode(4);
        root.left = new TreeNode(2);
        root.right = new TreeNode(5);
        root.left.left = new TreeNode(1);
        root.left.right = new TreeNode(3);

        ClosestValuesInBST solution = new ClosestValuesInBST();
        List<Integer> closestValues = solution.closestValues(root, 3.8, 2);
        System.out.println("Output: " + closestValues);
    }
}
