package com.example.demo.apps;


public class IndexedPriorityQueue<T> {
    private final T[] elements;
    private final int[] indices;
    private final int[] priorities;
    private int size;

    @SuppressWarnings("unchecked")
    public IndexedPriorityQueue(int capacity) {
        elements = (T[]) new Object[capacity + 1];
        indices = new int[capacity + 1];
        priorities = new int[capacity + 1];
        size = 0;
    }

    public void insert(int index, T element, int priority) {
        size++;
        indices[size] = index;
        elements[index] = element;
        priorities[index] = priority;
        swim(size);
    }

    public T removeMin() {
        T min = elements[indices[1]];
        swap(1, size);
        size--;
        sink(1);
        return min;
    }

    public void changePriority(int index, int newPriority) {
        priorities[index] = newPriority;
        swim(indices[index]);
        sink(indices[index]);
    }

    private void swim(int k) {
        while (k > 1 && priorities[k] < priorities[k / 2]) {
            swap(k, k / 2);
            k /= 2;
        }
    }

    private void sink(int k) {
        while (2 * k <= size) {
            int j = 2 * k;
            if (j < size && priorities[j + 1] < priorities[j]) {
                j++;
            }
            if (priorities[k] <= priorities[j]) {
                break;
            }
            swap(k, j);
            k = j;
        }
    }

    private void swap(int i, int j) {
        int tempIndex = indices[i];
        indices[i] = indices[j];
        indices[j] = tempIndex;
        T tempElement = elements[indices[i]];
        elements[indices[i]] = elements[indices[j]];
        elements[indices[j]] = tempElement;
        int tempPriority = priorities[indices[i]];
        priorities[indices[i]] = priorities[indices[j]];
        priorities[indices[j]] = tempPriority;
    }
}