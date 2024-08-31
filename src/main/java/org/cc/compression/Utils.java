package org.cc.compression;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

public class Utils {

    public static Map<Integer, Integer> getFrequencyOfText(String fileName) {
        Map<Integer, Integer> freq = new HashMap<>();
        File file = new File(fileName);
        try (FileInputStream fileReader = new FileInputStream(file)) {
            int character;
            while ((character = fileReader.read()) != -1) {
                freq.put(character, freq.getOrDefault(character, 0) + 1);
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

    public static TreeNode buildHuffmanPrefixTree(Map<Integer, Integer> freq) {
        PriorityQueue<TreeNode> minHeap = new PriorityQueue<>((x, y) -> (x.weight == y.weight) ? x.data - y.data : x.weight - y.weight);
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

    public static Map<Integer, String> buildHuffmanCodes(TreeNode huffmanTreeRoot) {
        Map<Integer, String> huffmanCodes = new HashMap<>();
        assigningHuffmanCodes(huffmanTreeRoot, huffmanCodes, new StringBuilder());
        return huffmanCodes;
    }

    private static void assigningHuffmanCodes(TreeNode huffmanTreeRoot, Map<Integer, String> huffmanCodes, StringBuilder stringBuilder) {
        if (huffmanTreeRoot.data != -1) {
            huffmanCodes.put(huffmanTreeRoot.data, stringBuilder.toString());
            return;
        }
        assigningHuffmanCodes(huffmanTreeRoot.left, huffmanCodes, new StringBuilder(stringBuilder).append("0"));
        assigningHuffmanCodes(huffmanTreeRoot.right, huffmanCodes, new StringBuilder(stringBuilder).append("1"));
    }
}
