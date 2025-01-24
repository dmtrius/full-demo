package com.example.demo.apps.compression;

import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.PriorityQueue;

public class HuffmanCoding {

    public HuffmanNode buildHuffmanTree(int[] frequency) {
        PriorityQueue<HuffmanNode> queue = new PriorityQueue<>();

        for (int i = 0; i < frequency.length; i++) {
            if (frequency[i] > 0) {
                queue.add(new HuffmanNode(frequency[i], (char) i, null, null));
            }
        }

        while (queue.size() > 1) {
            HuffmanNode left = queue.poll();
            HuffmanNode right = queue.poll();
            int sum = left.frequency + (right != null ? right.frequency : 0);
            queue.add(new HuffmanNode(sum, '\0', left, right));
        }

        return queue.poll();
    }

    public Map<Character, String> generateCodes(HuffmanNode root) {
        Map<Character, String> huffmanCodes = new HashMap<>();
        generateCodesHelper(root, "", huffmanCodes);
        return huffmanCodes;
    }

    private void generateCodesHelper(HuffmanNode node, String code, Map<Character, String> huffmanCodes) {
        if (node == null) {
            return;
        }

        if (node.left == null && node.right == null) {
            huffmanCodes.put(node.data, code);
        }

        generateCodesHelper(node.left, code + "0", huffmanCodes);
        generateCodesHelper(node.right, code + "1", huffmanCodes);
    }

    public BitSet compress(String input, Map<Character, String> huffmanCodes) {
        BitSet bitSet = new BitSet();
        int bitIndex = 0;

        for (char c : input.toCharArray()) {
            String code = huffmanCodes.get(c);
            for (char bit : code.toCharArray()) {
                bitSet.set(bitIndex++, bit == '1');
            }
        }

        return bitSet;
    }

    public String decompress(BitSet bitSet, HuffmanNode root) {
        StringBuilder result = new StringBuilder();
        HuffmanNode current = root;

        if (Objects.isNull(current)) {
            return result.toString();
        }

        for (int i = 0; i < bitSet.length(); i++) {
            current = bitSet.get(i) ? current.right : current.left;

            if (!Objects.isNull(current) && current.left == null && current.right == null) {
                result.append(current.data);
                current = root;
            }
        }

        return result.toString();
    }
}
