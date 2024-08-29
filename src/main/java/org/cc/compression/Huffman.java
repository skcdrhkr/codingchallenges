package org.cc.compression;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

public class Huffman {
    public static void main(String[] args) {

        Map<Character, Integer> freq = getFrequencyOfText(args[0]);
        TreeNode huffmanTreeRoot = buildHuffmanTree(freq);
        Map<Character, String> huffmanCodes = buildHuffmanCodes(huffmanTreeRoot);
    }

    public static Map<Character, String> buildHuffmanCodes(TreeNode huffmanTreeRoot) {
        Map<Character, String> huffmanCodes = new HashMap<>();
        assigningHuffmanCodes(huffmanTreeRoot, huffmanCodes, new StringBuilder());
        return huffmanCodes;
    }

    private static void assigningHuffmanCodes(TreeNode huffmanTreeRoot, Map<Character, String> huffmanCodes, StringBuilder stringBuilder) {
        if (huffmanTreeRoot.data != null) {
            huffmanCodes.put(huffmanTreeRoot.data, stringBuilder.toString());
            return;
        }
        assigningHuffmanCodes(huffmanTreeRoot.left, huffmanCodes, new StringBuilder(stringBuilder).append("0"));
        assigningHuffmanCodes(huffmanTreeRoot.right, huffmanCodes, new StringBuilder(stringBuilder).append("1"));
    }

    public static TreeNode buildHuffmanTree(Map<Character, Integer> freq) {
        PriorityQueue<TreeNode> minHeap = new PriorityQueue<>((x, y) -> x.weight == y.weight ? x.data - y.data : x.weight - y.weight);
        freq.keySet().forEach(key -> minHeap.add(new TreeNode(freq.get(key), key)));
        while (minHeap.size() > 1) {
            TreeNode topFirst = minHeap.poll();
            TreeNode topSecond = minHeap.poll();
            TreeNode parent = new TreeNode(topFirst.weight + topSecond.weight);
            parent.left = topFirst;
            parent.right = topSecond;
            minHeap.add(parent);
        }
        return minHeap.poll();
    }

    public static Map<Character, Integer> getFrequencyOfText(String fileName) {
        Map<Character, Integer> freq = new HashMap<>();
        File file = new File(fileName);
        try (FileReader fileReader = new FileReader(file, StandardCharsets.UTF_8)) {
            int character;
            while ((character = fileReader.read()) != -1) {
                freq.put((char) character, freq.getOrDefault((char) character, 0) + 1);
            }

        } catch (FileNotFoundException e) {
            System.out.println("Invalid file");
            throw new RuntimeException(e);
        } catch (IOException e) {
            System.out.printf("Exception thrown while reading the file %s%n", file.getName());
            throw new RuntimeException(e);
        }
        return freq;
    }
}
