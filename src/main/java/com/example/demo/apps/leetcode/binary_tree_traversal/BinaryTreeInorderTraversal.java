package com.example.demo.apps.leetcode.binary_tree_traversal;

import com.example.demo.apps.leetcode.TreeNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import static java.lang.IO.println;

/**
 * Binary Tree Traversal (<a href="https://leetcode.com/problems/binary-tree-inorder-traversal/">94</a>)
 * <a href="https://algo.monster/liteproblems/94">94</a>
 * <h2>Binary Tree Inorder Traversal</h2>
 * <p>
 * Given the root of a binary tree, return the inorder traversal of its nodes' values.
 * <p>
 * Example 1:
 * <p>
 * Input: root = [1,null,2,3]
 * <p>
 * Output: [1,3,2]
 * <p>
 * Explanation:
 * <p>
 * Example 2:
 * <p>
 * Input: root = [1,2,3,4,5,null,8,null,null,6,7,9]
 * <p>
 * Output: [4,2,6,5,7,1,3,9,8]
 * <p>
 * Explanation:
 * <p>
 * Example 3:
 * <p>
 * Input: root = []
 * <p>
 * Output: []
 * <p>
 * Example 4:
 * <p>
 * Input: root = [1]
 * <p>
 * Output: [1]
 */
public class BinaryTreeInorderTraversal {
    void main() {
        TreeNode root = new TreeNode(3);
        root.left = new TreeNode(9);
        root.right = new TreeNode(20);
        root.right.left = new TreeNode(15);
        root.right.right = new TreeNode(7);
        println(inorderTraversal(root));
        println(inorderTraversalStack(root));
        println(inorderTraversalMorris(root));
    }

    List<Integer> inorderTraversal(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        inorder(root, result);
        return result;
    }

    private void inorder(TreeNode node, List<Integer> result) {
        if (node == null) {
            return;
        }

        inorder(node.left, result);   // Visit left subtree
        result.add(node.val);          // Visit root
        inorder(node.right, result);   // Visit right subtree
    }

    List<Integer> inorderTraversalStack(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        Stack<TreeNode> stack = new Stack<>();
        TreeNode current = root;

        while (current != null || !stack.isEmpty()) {
            // Go to the leftmost node
            while (current != null) {
                stack.push(current);
                current = current.left;
            }

            // Current is null, so pop from stack
            current = stack.pop();
            result.add(current.val);  // Visit the node

            // Visit right subtree
            current = current.right;
        }

        return result;
    }

    List<Integer> inorderTraversalMorris(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        TreeNode current = root;

        while (current != null) {
            if (current.left == null) {
                // No left subtree, visit current and go right
                result.add(current.val);
                current = current.right;
            } else {
                // Find the inorder predecessor
                TreeNode predecessor = current.left;
                while (predecessor.right != null && predecessor.right != current) {
                    predecessor = predecessor.right;
                }

                if (predecessor.right == null) {
                    // Create thread
                    predecessor.right = current;
                    current = current.left;
                } else {
                    // Thread exists, visit and remove it
                    predecessor.right = null;
                    result.add(current.val);
                    current = current.right;
                }
            }
        }

        return result;
    }
}
