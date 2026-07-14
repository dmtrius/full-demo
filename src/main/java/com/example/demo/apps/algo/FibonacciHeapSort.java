package com.example.demo.apps.algo;

import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Fibonacci Heap implementation supporting insert, findMin, extractMin,
 * decreaseKey, delete, union, and a heapSort utility built on top of it.
 * <p>
 * Amortized complexities:
 *   insert         O(1)
 *   findMin        O(1)
 *   union/merge    O(1)
 *   decreaseKey    O(1)
 *   extractMin     O(log n)
 *   delete         O(log n)
 * <p>
 * heapSort using this structure runs in O(n log n).
 */
public class FibonacciHeapSort<T extends Comparable<T>> {

    /**
     * Node of the Fibonacci heap's circular doubly linked list.
     */
    @Data
    private class Node {
        private T key;
        private Node child;
        private Node left;
        private Node right;
        private int degree;
        private boolean mark;
        private Node parent;

        Node(T key) {
            this.key = key;
            this.left = this;
            this.right = this;
        }
    }

    private Node minNode;
    private int size;

    public FibonacciHeapSort() {
        minNode = null;
        size = 0;
    }

    public boolean isEmpty() {
        return Objects.isNull(minNode);
    }

    public int size() {
        return size;
    }

    /**
     * Inserts a new key into the heap. Returns the created node reference (opaque handle)
     */
    private Node insert(T key) {
        Node node = new Node(key);
        minNode = mergeLists(minNode, node);
        size++;
        return node;
    }

    /**
     * Returns the minimum key without removing it.
     */
    public T findMin() {
        if (Objects.isNull(minNode)) {
            throw new IllegalStateException("Heap is empty");
        }
        return minNode.key;
    }

    /**
     * Merges another Fibonacci heap into this one in O(1).
     */
    @SuppressWarnings("unused")
    public void union(FibonacciHeapSort<T> other) {
        if (Objects.isNull(other) || Objects.isNull(other.minNode)) {
            return;
        }
        minNode = mergeLists(minNode, other.minNode);
        size += other.size();
        other.minNode = null;
        other.size = 0;
    }

    /**
     * Removes and returns the minimum key from the heap.
     */
    public T extractMin() {
        Node z = minNode;
        if (Objects.isNull(z)) {
            throw new IllegalStateException("Heap is empty");
        }

        // Move all children of z to the root list.
        if (Objects.nonNull(z.child)) {
            // Collect children first so relinking doesn't corrupt traversal.
            List<Node> children = new ArrayList<>();
            Node child = z.getChild();
            do {
                children.add(child);
                child = child.getRight();
            } while (child != z.getChild());

            for (Node c : children) {
                removeFromList(c);
                c.setLeft(c);
                c.setRight(c);
                c.setParent(null);
                minNode = mergeLists(minNode, c);
            }
        }

        removeFromList(z);
        if (z == z.getRight()) {
            minNode = null;
        } else {
            minNode = z.getRight();
            consolidate();
        }

        size--;
        return z.getKey();
    }

    /**
     * Consolidates the root list so that no two roots share the same degree.
     */
    private void consolidate() {
        int maxDegree = ((int) Math.floor(Math.log(size + 1) / Math.log(2))) + 2;
        List<Node> degreeTable = new ArrayList<>(maxDegree);
        for (int i = 0; i < maxDegree; i++) {
            degreeTable.add(null);
        }

        List<Node> roots = new ArrayList<>();
        Node current = minNode;
        if (Objects.nonNull(current)) {
            do {
                roots.add(current);
                current = current.getRight();
            } while (current != minNode);
        }

        for (Node w : roots) {
            Node x = w;
            int d = x.getDegree();
            while (d < degreeTable.size() && Objects.nonNull(degreeTable.get(d))) {
                Node y = degreeTable.get(d);
                if (x.getKey().compareTo(y.getKey()) > 0) {
                    Node tmp = x;
                    x = y;
                    y = tmp;
                }
                link(y, x);
                degreeTable.set(d, null);
                d++;
                if (d >= degreeTable.size()) {
                    degreeTable.add(null);
                }
            }
            if (d >= degreeTable.size()) {
                degreeTable.add(x);
            } else {
                degreeTable.set(d, x);
            }
        }

        minNode = null;
        for (Node node : degreeTable) {
            if (Objects.nonNull(node)) {
                node.setLeft(node);
                node.setRight(node);
                minNode = mergeLists(minNode, node);
            }
        }
    }

    /**
     * Makes y a child of x (used during consolidation).
     */
    private void link(Node y, Node x) {
        removeFromList(y);
        y.setLeft(y);
        y.setRight(y);
        x.setChild(mergeLists(x.child, y));
        y.setParent(x);
        x.setDegree(x.getDegree() + 1);
        y.setMark(false);
    }

    /**
     * Decreases the key of the given node handle to newKey.
     */
    @SuppressWarnings("unchecked")
    public void decreaseKey(Object nodeHandle, T newKey) {
        Node x = (Node) nodeHandle;
        if (newKey.compareTo(x.getKey()) > 0) {
            throw new IllegalArgumentException("New key is greater than current key");
        }
        x.setKey(newKey);
        Node y = x.getParent();
        if (Objects.nonNull(y) && x.getKey().compareTo(y.getKey()) < 0) {
            cut(x, y);
            cascadingCut(y);
        }
        if (x.getKey().compareTo(minNode.getKey()) < 0) {
            minNode = x;
        }
    }

    private void cut(Node x, Node y) {
        removeFromList(x);
        y.setDegree(y.getDegree() - 1);
        if (y.getDegree() == 0) {
            y.setChild(null);
        } else if (y.getChild() == x) {
            y.setChild(x.getRight());
        }
        x.setParent(null);
        x.setMark(false);
        x.setLeft(x);
        x.setRight(x);
        minNode = mergeLists(minNode, x);
    }

    private void cascadingCut(Node y) {
        Node z = y.getParent();
        if (Objects.nonNull(z)) {
            if (!y.isMark()) {
                y.setMark(true);
            } else {
                cut(y, z);
                cascadingCut(z);
            }
        }
    }

    /**
     * Deletes an arbitrary node from the heap given its handle.
     */
    @SuppressWarnings("unused")
    public void delete(Object nodeHandle, T minusInfinity) {
        decreaseKey(nodeHandle, minusInfinity);
        extractMin();
    }

    // — circular doubly linked list helpers —

    /**
     * Merges two circular doubly linked root lists; returns the node with the smaller key.
     */
    private Node mergeLists(Node a, Node b) {
        if (Objects.isNull(a)) {
            return b;
        }
        if (Objects.isNull(b)) {
            return a;
        }

        Node aRight = a.getRight();
        Node bLeft = b.getLeft();
        a.setRight(b);
        b.setLeft(a);
        aRight.setLeft(bLeft);
        bLeft.setRight(aRight);

        return a.getKey().compareTo(b.getKey()) <= 0 ? a : b;
    }

    /**
     * Removes a node from its circular doubly linked list.
     */
    private void removeFromList(Node x) {
        x.getLeft().setRight(x.getRight());
        x.getRight().setLeft(x.getLeft());
    }

    // — Heap sort —

    /**
     * Sorts the given array in ascending order using a Fibonacci heap.
     * Builds the heap in O(n) via repeated insert, then performs n
     * extractMin calls, each O(log n) amortized, for O(n log n) overall.
     */
    public static <E extends Comparable<E>> void heapSort(E[] array) {
        if (Objects.isNull(array) || array.length == 0) {
            return;
        }
        FibonacciHeapSort<E> heap = new FibonacciHeapSort<>();
        for (E element : array) {
            heap.insert(element);
        }
        for (int i = 0; i < array.length; i++) {
            array[i] = heap.extractMin();
        }
    }

    // --- demo / manual test ---

    void main() {
        Integer[] data = {23, 4, 17, 9, 42, 1, 8, 15, 30, -5, 0, 99, 7};
        IO.println("Before: " + Arrays.toString(data));
        heapSort(data);
        IO.println("After:  " + Arrays.toString(data));

        // Quick correctness check
        boolean sorted = true;
        for (int i = 1; i < data.length; i++) {
            if (data[i - 1].compareTo(data[i]) > 0) {
                sorted = false;
                break;
            }
        }
        IO.println("Sorted correctly: " + sorted);

        // Demonstrate decreaseKey usage separately
        FibonacciHeapSort<Integer> heap = new FibonacciHeapSort<>();
        Object h10 = heap.insert(10);
        heap.insert(20);
        heap.insert(5);
        heap.decreaseKey(h10, 1);
        IO.println("Min after decreaseKey: " + heap.findMin()); // expect 1
    }
}
