package com.example.demo.apps.compression;

import java.util.Objects;

public class HuffmanNode implements Comparable<HuffmanNode> {
    int frequency;
    char data;
    HuffmanNode left;
    HuffmanNode right;

    public HuffmanNode(int frequency, char data, HuffmanNode left, HuffmanNode right) {
        this.frequency = frequency;
        this.data = data;
        this.left = left;
        this.right = right;
    }

    @Override
    public int compareTo(HuffmanNode other) {
        return this.frequency - other.frequency;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        HuffmanNode that = (HuffmanNode) o;
        return frequency == that.frequency && data == that.data;
    }

    @Override
    public int hashCode() {
        return Objects.hash(frequency, data);
    }
}
