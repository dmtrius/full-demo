package com.example.demo.apps.algo;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;

public class App1 {
    public static void main(String... args) {
        testHeight();
        testDFS();
        testBFS();
        testLevelTraversal();
    }

    public static void testHeight() {
        TreeNode<Integer> root = getRoot();
        System.out.println("Height of the tree:");
        System.out.println(height(root));
    }

    public static void testBFS() {
        TreeNode<Integer> root = getRoot();
        System.out.println("BFS traversal of the tree:");
        bfs(root);
        System.out.println();
    }

    public static void testDFS() {
        TreeNode<Integer> root = getRoot();
        System.out.println("DFS traversal of the tree:");
        dfs(root);
        System.out.println();
    }

    public static void testLevelTraversal() {
        TreeNode<Integer> root = getRoot();
        List<List<Integer>> result = levelOrder(root);
        System.out.println("Level-order traversal of the tree:");
        for (List<Integer> level : result) {
            System.out.println(level);
        }
    }

    private static <T> int height(TreeNode<T> root) {
        if (root == null) {
            return 0;
        }

        int leftHeight = (Objects.isNull(root.left)) ? 0 : height(root.left);
        int rightHeight = (Objects.isNull(root.right)) ? 0 : height(root.right);

        return Math.max(leftHeight, rightHeight) + 1;
    }

    private static <T> void dfs(TreeNode<T> root) {
        if (root == null) {
            return;
        }
        System.out.print(root.value + " ");
        dfs(root.left);
        dfs(root.right);
    }


    private static <T> void bfs(TreeNode<T> root) {
        if (root == null) {
            return;
        }

        Queue<TreeNode<T>> queue = new LinkedList<>();
        queue.add(root);

        while (!queue.isEmpty()) {
            TreeNode<T> node = queue.poll();
            System.out.print(node.value + " ");

            if (node.left != null) {
                queue.add(node.left);
            }
            if (node.right != null) {
                queue.add(node.right);
            }
        }
    }

    private static List<List<Integer>> levelOrder(TreeNode<Integer> root) {
        List<List<Integer>> result = new ArrayList<>();
        if (root == null) {
            return result;
        }

        Queue<TreeNode<Integer>> queue = new LinkedList<>();
        queue.add(root);

        while (!queue.isEmpty()) {
            int levelSize = queue.size();
            List<Integer> currentLevel = new ArrayList<>();

            for (int i = 0; i < levelSize; i++) {
                TreeNode<Integer> node = queue.poll();
                if (null == node) {
                    continue;
                }
                currentLevel.add(node.value);

                if (node.left != null) {
                    queue.add(node.left);
                }
                if (node.right != null) {
                    queue.add(node.right);
                }
            }

            result.add(currentLevel);
        }

        return result;
    }

    private static TreeNode<Integer> getRoot() {
        TreeNode<Integer> root = new TreeNode<>(1);
        root.left = new TreeNode<>(2);
        root.right = new TreeNode<>(3);
        root.left.left = new TreeNode<>(4);
        root.left.right = new TreeNode<>(5);
        root.right.left = new TreeNode<>(6);
        root.right.right = new TreeNode<>(7);

        return root;
    }
}

class TreeNode<T> {
    T value;
    TreeNode<T> left, right;

    public TreeNode(T value) {
        this.value = value;
        this.left = this.right = null;
    }
}