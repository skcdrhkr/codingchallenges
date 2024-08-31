package org.cc.compression;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.stream.Collectors;

import static org.cc.compression.Constants.*;
import static org.cc.compression.Utils.*;

public class Encoder {

    public static void compressFile(String inputFile, String outputFile) {

        Map<Integer, Integer> freq = getFrequencyOfText(inputFile);
        TreeNode huffmanPrefixTreeRoot = buildHuffmanPrefixTree(freq);
        Map<Integer, String> huffmanPrefixCodes = buildHuffmanCodes(huffmanPrefixTreeRoot);

        String huffmanHeader = createHuffmanEncodingHeader(freq);

        writeHeaderToOutputFile(huffmanHeader, outputFile);
        encodeFileDataToOutputFile(inputFile, outputFile, huffmanPrefixCodes);
    }


    private static void encodeFileDataToOutputFile(String inputFile, String outputFile, Map<Integer, String> huffmanCodes) {
        File input = new File(inputFile);
        File output = new File(outputFile);
        StringBuilder buffer = new StringBuilder();
        try (FileInputStream fileReader = new FileInputStream(input); FileOutputStream fileWriter = new FileOutputStream(output, true)) {
            int character;
            while ((character = fileReader.read()) != -1) {
                String prefix = huffmanCodes.get(character);
                buffer.append(prefix);

                // If we get 8 bits, we pack it into byte and write it to file
                if (buffer.length() > 8) {
                    String firstByte = buffer.substring(0, 8);
                    buffer.delete(0, 8);
                    fileWriter.write((byte) Integer.parseInt(firstByte, 2));
                }
            }
            if (!buffer.isEmpty()) {
                fileWriter.write((byte) Integer.parseInt(buffer.toString(), 2));
            }
        } catch (IOException e) {
            System.out.println("Exception thrown while writing output file.");
            throw new RuntimeException(e);
        }
    }

    public static String createHuffmanEncodingHeader(Map<Integer, Integer> freq) {
        return freq.entrySet()
                .stream()
                .map(x -> x.getKey() + KEY_VALUE_SEP + x.getValue())
                .collect(Collectors.joining(ENTRY_SEP, HEADER_TERMINAL, HEADER_TERMINAL));
    }

    private static void writeHeaderToOutputFile(String huffmanHeader, String outputFile) {
        File file = new File(outputFile);
        try (FileOutputStream fileWriter = new FileOutputStream(file)) {
            fileWriter.write(huffmanHeader.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            System.out.println("Exception thrown while writing encoding header file.");
            throw new RuntimeException(e);
        }
    }
}
