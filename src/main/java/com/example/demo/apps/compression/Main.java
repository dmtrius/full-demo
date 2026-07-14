package com.example.demo.apps.compression;

import java.util.BitSet;
import java.util.Map;

public class Main {
    void main() {
        String input = "this is an example for huffman encoding";
        int[] frequency = new int[256];

        for (char c : input.toCharArray()) {
            frequency[c]++;
        }

        HuffmanCoding huffmanCoding = new HuffmanCoding();
        HuffmanNode root = huffmanCoding.buildHuffmanTree(frequency);
        Map<Character, String> huffmanCodes = huffmanCoding.generateCodes(root);

        BitSet compressed = huffmanCoding.compress(input, huffmanCodes);
        IO.println("Compressed: " + compressed);

        String decompressed = huffmanCoding.decompress(compressed, root);
        IO.println("Decompressed: " + decompressed);
    }
}
