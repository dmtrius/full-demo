package com.example.demo.apps.algo;

import java.util.ArrayList;
import java.util.List;

/**
 * Fibonacci Heap implementation supporting insert, findMin, extractMin,
 * decreaseKey, delete, union, and a heapSort utility built on top of it.
 *
 * Amortized complexities:
 *   insert         O(1)
 *   findMin        O(1)
 *   union/merge    O(1)
 *   decreaseKey    O(1)
 *   extractMin     O(log n)
 *   delete         O(log n)
 *
 * heapSort using this structure runs in O(n log n).
 */
public class FibonacciHeapSort<T extends Comparable<T>> {

    /** Node of the Fibonacci heap's circular doubly linked list. */
    private class Node {
        T key;
        Node parent;
        Node child;
        Node left;
        Node right;
        int degree;
        boolean mark;

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
        return minNode == null;
    }

    public int size() {
        return size;
    }

    /** Inserts a new key into the heap. Returns the created node reference (opaque handle). */
    public Object insert(T key) {
        Node node = new Node(key);
        minNode = mergeLists(minNode, node);
        size++;
        return node;
    }

    /** Returns the minimum key without removing it. */
    public T findMin() {
        if (minNode == null) {
            throw new IllegalStateException("Heap is empty");
        }
        return minNode.key;
    }

    /** Merges another Fibonacci heap into this one in O(1). */
    public void union(FibonacciHeapSort<T> other) {
        if (other == null || other.minNode == null) {
            return;
        }
        minNode = mergeLists(minNode, other.minNode);
        size += other.size;
        other.minNode = null;
        other.size = 0;
    }

    /** Removes and returns the minimum key from the heap. */
    public T extractMin() {
        Node z = minNode;
        if (z == null) {
            throw new IllegalStateException("Heap is empty");
        }

        // Move all children of z to the root list.
        if (z.child != null) {
            // Collect children first so relinking doesn't corrupt traversal.
            List<Node> children = new ArrayList<>();
            Node child = z.child;
            do {
                children.add(child);
                child = child.right;
            } while (child != z.child);

            for (Node c : children) {
                removeFromList(c);
                c.left = c;
                c.right = c;
                c.parent = null;
                minNode = mergeLists(minNode, c);
            }
        }

        removeFromList(z);
        if (z == z.right) {
            minNode = null;
        } else {
            minNode = z.right;
            consolidate();
        }

        size--;
        return z.key;
    }

    /** Consolidates the root list so that no two roots share the same degree. */
    private void consolidate() {
        int maxDegree = ((int) Math.floor(Math.log(size + 1) / Math.log(2))) + 2;
        List<Node> degreeTable = new ArrayList<>(maxDegree);
        for (int i = 0; i < maxDegree; i++) {
            degreeTable.add(null);
        }

        List<Node> roots = new ArrayList<>();
        Node current = minNode;
        if (current != null) {
            do {
                roots.add(current);
                current = current.right;
            } while (current != minNode);
        }

        for (Node w : roots) {
            Node x = w;
            int d = x.degree;
            while (d < degreeTable.size() && degreeTable.get(d) != null) {
                Node y = degreeTable.get(d);
                if (x.key.compareTo(y.key) > 0) {
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
            if (node != null) {
                node.left = node;
                node.right = node;
                minNode = mergeLists(minNode, node);
            }
        }
    }

    /** Makes y a child of x (used during consolidation). */
    private void link(Node y, Node x) {
        removeFromList(y);
        y.left = y;
        y.right = y;
        x.child = mergeLists(x.child, y);
        y.parent = x;
        x.degree++;
        y.mark = false;
    }

    /** Decreases the key of the given node handle to newKey. */
    @SuppressWarnings("unchecked")
    public void decreaseKey(Object nodeHandle, T newKey) {
        Node x = (Node) nodeHandle;
        if (newKey.compareTo(x.key) > 0) {
            throw new IllegalArgumentException("New key is greater than current key");
        }
        x.key = newKey;
        Node y = x.parent;
        if (y != null && x.key.compareTo(y.key) < 0) {
            cut(x, y);
            cascadingCut(y);
        }
        if (x.key.compareTo(minNode.key) < 0) {
            minNode = x;
        }
    }

    private void cut(Node x, Node y) {
        removeFromList(x);
        y.degree--;
        if (y.degree == 0) {
            y.child = null;
        } else if (y.child == x) {
            y.child = x.right;
        }
        x.parent = null;
        x.mark = false;
        x.left = x;
        x.right = x;
        minNode = mergeLists(minNode, x);
    }

    private void cascadingCut(Node y) {
        Node z = y.parent;
        if (z != null) {
            if (!y.mark) {
                y.mark = true;
            } else {
                cut(y, z);
                cascadingCut(z);
            }
        }
    }

    /** Deletes an arbitrary node from the heap given its handle. */
    public void delete(Object nodeHandle, T minusInfinity) {
        decreaseKey(nodeHandle, minusInfinity);
        extractMin();
    }

    // --- circular doubly linked list helpers ---

    /** Merges two circular doubly linked root lists; returns the node with the smaller key. */
    private Node mergeLists(Node a, Node b) {
        if (a == null) return b;
        if (b == null) return a;

        Node aRight = a.right;
        Node bLeft = b.left;
        a.right = b;
        b.left = a;
        aRight.left = bLeft;
        bLeft.right = aRight;

        return a.key.compareTo(b.key) <= 0 ? a : b;
    }

    /** Removes a node from its circular doubly linked list. */
    private void removeFromList(Node x) {
        x.left.right = x.right;
        x.right.left = x.left;
    }

    // --- Heap sort ---

    /**
     * Sorts the given array in ascending order using a Fibonacci heap.
     * Builds the heap in O(n) via repeated insert, then performs n
     * extractMin calls, each O(log n) amortized, for O(n log n) overall.
     */
    public static <E extends Comparable<E>> void heapSort(E[] array) {
        if (array == null || array.length == 0) {
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

    public static void main(String[] args) {
        Integer[] data = {23, 4, 17, 9, 42, 1, 8, 15, 30, -5, 0, 99, 7};
        System.out.println("Before: " + java.util.Arrays.toString(data));
        heapSort(data);
        System.out.println("After:  " + java.util.Arrays.toString(data));

        // Quick correctness check
        boolean sorted = true;
        for (int i = 1; i < data.length; i++) {
            if (data[i - 1].compareTo(data[i]) > 0) {
                sorted = false;
                break;
            }
        }
        System.out.println("Sorted correctly: " + sorted);

        // Demonstrate decreaseKey usage separately
        FibonacciHeapSort<Integer> heap = new FibonacciHeapSort<>();
        Object h10 = heap.insert(10);
        heap.insert(20);
        heap.insert(5);
        heap.decreaseKey(h10, 1);
        System.out.println("Min after decreaseKey: " + heap.findMin()); // expect 1
    }
}