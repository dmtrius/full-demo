package com.example.demo.apps.leetcode.binary_tree_traversal;

import com.example.demo.apps.leetcode.TreeNode;

import java.util.LinkedList;
import java.util.Queue;

import static java.lang.IO.println;

/**
 * Binary Tree Traversal (<a href="https://leetcode.com/problems/maximum-depth-of-binary-tree/">104</a>)
 * <a href="https://algo.monster/liteproblems/104">104</a>
 *
 * <h2>Maximum Depth of Binary Tree</h2>
 * Given the root of a binary tree, return its maximum depth.
 * A binary tree's maximum depth is the number of nodes along the longest path from the root node down to the farthest leaf node.
 * <p>
 * Example 1:
 * <p>
 * Input: root = [3,9,20,null,null,15,7]
 * <p>
 * Output: 3
 * <p>
 * Example 2:
 * <p>
 * Input: root = [1,null,2]
 * <p>
 * Output: 2
 */
public class MaximumDepthOfBinaryTree {
    void main() {
        TreeNode root = new TreeNode(3);
        root.left = new TreeNode(9);
        root.right = new TreeNode(20);
        root.right.left = new TreeNode(15);
        root.right.right = new TreeNode(7);
        println(maxDepth(root));
        println(maxDepthBFS(root));
    }

    int maxDepth(TreeNode root) {
        if (root == null) return 0;
        int leftDepth = maxDepth(root.left);
        int rightDepth = maxDepth(root.right);
        return 1 + Math.max(leftDepth, rightDepth);
    }

    int maxDepthBFS(TreeNode root) {
        if (root == null) return 0;
        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);
        int depth = 0;
        while (!queue.isEmpty()) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                TreeNode node = queue.poll();
                if (node.left != null) queue.offer(node.left);
                if (node.right != null) queue.offer(node.right);
            }
            depth++;
        }
        return depth;
    }
}
