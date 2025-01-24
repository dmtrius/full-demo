package com.example.demo.apps;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;

import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;

@Log4j2
public class App1 {
    public static void main(String... args) {
        Node<Integer> root = new Node<>(1);
        root.left = new Node<>(2);
        root.right = new Node<>(3);
        root.left.left = new Node<>(4);
        root.left.right = new Node<>(5);
        root.right.left = new Node<>(6);
        root.right.right = new Node<>(7);

//        log.info("Preorder traversal:");
//        preorder(root);
//        log.info("Inorder traversal:");
//        inorder(root);
//        log.info("Postorder traversal:");
//        postorder(root);
        log.info("Level_order traversal:");
        levelOrder(root);
    }

    private static void preorder(Node<Integer> node) {
        if (!Objects.isNull(node)) {
            node.visit();
            preorder(node.left);
            preorder(node.right);
        }
    }

    private static void inorder(Node<Integer> node) {
        if (!Objects.isNull(node)) {
            inorder(node.left);
            node.visit();
            inorder(node.right);
        }
    }

    private static void postorder(Node<Integer> node) {
        if (!Objects.isNull(node)) {
            postorder(node.left);
            postorder(node.right);
            node.visit();
        }
    }

    private static void levelOrder(Node<Integer> node) {
        Queue<Node<Integer>> queue = new LinkedList<>();
        queue.offer(node);
        while (!queue.isEmpty()) {
            Node<Integer> next = queue.remove();
            next.visit();
            if (next.left != null)
                queue.add(next.left);
            if (next.right != null)
                queue.add(next.right);
        }
    }
}

@Log4j2
@EqualsAndHashCode
@ToString
class Node<T> {
    Node<T> left;
    Node<T> right;
    T data;

    public Node(T data) {
        this.data = data;
    }

    public void visit() {
        log.info(data);
//        log.info("left: {}", !Objects.isNull(left) ? left.data : null);
//        log.info("right: {}", !Objects.isNull(right) ? right.data : null);
    }
}
