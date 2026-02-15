package com.example.demo.apps.leetcode.dfs;

import com.example.demo.apps.leetcode.TreeNode;

import java.util.Stack;

import static java.lang.IO.println;

/**
 * DFS (<a href="https://leetcode.com/problems/path-sum/">112</a>)
 * <a href="https://algo.monster/liteproblems/112">112</a>
 * <h2>Path Sum</h2>
 * Given the root of a binary tree and an integer targetSum, return true if the tree has a root-to-leaf path such that adding up all the values along the path equals targetSum.
 * <p>
 * A leaf is a node with no children.
 * <p>
 * Example 1:
 * <p>
 * Input: root = [5,4,8,11,null,13,4,7,2,null,null,null,1], targetSum = 22
 * Output: true
 * Explanation: The root-to-leaf path with the target sum is shown.
 * Example 2:
 * <p>
 * Input: root = [1,2,3], targetSum = 5
 * Output: false
 * Explanation: There are two root-to-leaf paths in the tree:
 * (1 --> 2): The sum is 3.
 * (1 --> 3): The sum is 4.
 * There is no root-to-leaf path with sum = 5.
 * Example 3:
 * <p>
 * Input: root = [], targetSum = 0
 * Output: false
 * Explanation: Since the tree is empty, there are no root-to-leaf paths.
 */
public class PathSum {
    void main() {
        TreeNode root = new TreeNode(3);
        root.left = new TreeNode(9);
        root.right = new TreeNode(20);
        root.right.left = new TreeNode(15);
        root.right.right = new TreeNode(7);
        println(hasPathSum(root, 12));
        println(hasPathSum(root, 121));
    }

    boolean hasPathSum(TreeNode root, int targetSum) {
        // Base case: empty tree
        if (root == null) {
            return false;
        }

        // Base case: leaf node - check if current value equals remaining sum
        if (root.left == null && root.right == null) {
            return root.val == targetSum;
        }

        // Recursive case: check left and right subtrees with reduced target
        int remainingSum = targetSum - root.val;
        return hasPathSum(root.left, remainingSum) || hasPathSum(root.right, remainingSum);
    }

    boolean hasPathSumIterative(TreeNode root, int targetSum) {
        if (root == null) {
            return false;
        }

        Stack<TreeNode> nodeStack = new Stack<>();
        Stack<Integer> sumStack = new Stack<>();

        nodeStack.push(root);
        sumStack.push(targetSum - root.val);

        while (!nodeStack.isEmpty()) {
            TreeNode node = nodeStack.pop();
            int currentSum = sumStack.pop();

            // Check if it's a leaf node with the target sum
            if (node.left == null && node.right == null && currentSum == 0) {
                return true;
            }

            // Add right child
            if (node.right != null) {
                nodeStack.push(node.right);
                sumStack.push(currentSum - node.right.val);
            }

            // Add left child
            if (node.left != null) {
                nodeStack.push(node.left);
                sumStack.push(currentSum - node.left.val);
            }
        }

        return false;
    }
}
