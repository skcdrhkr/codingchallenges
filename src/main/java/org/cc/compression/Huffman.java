package org.cc.compression;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.stream.Collectors;

import static org.cc.compression.Constants.*;

public class Huffman {
    public static void main(String[] args) {

        String inputFile = args[0];
        String outputFile = args[1];

        Map<Character, Integer> freq = getFrequencyOfText(inputFile);
        TreeNode huffmanTreeRoot = buildHuffmanTree(freq);
        Map<Character, String> huffmanCodes = buildHuffmanCodes(huffmanTreeRoot);
        String huffmanHeader = getHuffmanCompressionHeader(freq);
        writeHeaderToOutputFile(huffmanHeader, outputFile);

        compressFileWithHufman(inputFile, outputFile, huffmanCodes);

//        Map<String, String> compressFreq = readCompressedFileHeader(outputFile);
//        System.out.println(compressFreq);
    }

    private static void compressFileWithHufman(String inputFile, String outputFile, Map<Character, String> huffmanCodes) {
        File input = new File(inputFile);
        File output = new File(outputFile);
        StringBuilder buffer = new StringBuilder();
        try (FileReader fileReader = new FileReader(input, StandardCharsets.UTF_8); FileOutputStream fileWriter = new FileOutputStream(output, true)) {
            int character;
            while ((character = fileReader.read()) != -1) {
                String prefix = huffmanCodes.get((char) character);
                buffer.append(prefix);
                if (buffer.length() > 8) {
                    String firstByte = buffer.substring(0, 8);
                    buffer.delete(0, 8);
                    fileWriter.write((byte) Integer.parseInt(firstByte, 2));
                }
            }
            if (!buffer.isEmpty()) {
                buffer.insert(0, "0".repeat(8 - buffer.length()));
                fileWriter.write((byte) Integer.parseInt(buffer.toString(), 2));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static Map<String, String> readCompressedFileHeader(String outputFile) {
        String compressedData = readCompressedFile(outputFile);
        String[] headerEntries = compressedData.replaceAll(HEADER_TERMINAL, "").split(ENTRY_SEP);
        return Arrays.stream(headerEntries).map(x -> x.split(KEY_VALUE_SEP)).collect(Collectors.toMap(x -> x[0], x -> x[1]));
    }

    private static String readCompressedFile(String outputFile) {
        Path outputPath = Paths.get(outputFile);
        String fileContent = null;
        try {
            fileContent = Files.readString(outputPath, StandardCharsets.UTF_8);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return fileContent;
    }

    private static void writeHeaderToOutputFile(String huffmanHeader, String outputFile) {
        File file = new File(outputFile);
        try (FileWriter fileWriter = new FileWriter(file, StandardCharsets.UTF_8)) {
            fileWriter.write(huffmanHeader);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getHuffmanCompressionHeader(Map<Character, Integer> freq) {
        StringBuilder header = new StringBuilder();
        return freq.entrySet()
                .stream()
                .map(x -> x.getKey() + KEY_VALUE_SEP + x.getValue())
                .collect(Collectors.joining(ENTRY_SEP, HEADER_TERMINAL, HEADER_TERMINAL));
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
        PriorityQueue<TreeNode> minHeap = new PriorityQueue<>((x, y) -> (x.weight == y.weight && x.data != null && y.data != null) ? x.data - y.data : x.weight - y.weight);
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
