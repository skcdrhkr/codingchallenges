package org.cc.compression;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import static org.cc.compression.Constants.*;
import static org.cc.compression.Utils.buildHuffmanPrefixTree;

public class Decoder {

    public static void extractFile(String input, String output) {
        FileContent compressedData = getCompressedFileContent(input);
        Map<Integer, Integer> charFreq = getFrequencyMapFromFileHeader(compressedData.header);

        TreeNode huffmanTreeRoot = buildHuffmanPrefixTree(charFreq);
        String bodyBitStream = getBodyBitStream(compressedData.body);

        byte[] getBodyContent = decodeFileContentToBytes(huffmanTreeRoot, bodyBitStream);

        writeDecodedOutputToFile(getBodyContent, output);
    }

    private static Map<Integer, Integer> getFrequencyMapFromFileHeader(String header) {
        String headerStream = header.replace(HEADER_TERMINAL, "");
        String[] headerEntries = headerStream.split(ENTRY_SEP);
        return Arrays.stream(headerEntries).map(x -> x.split(KEY_VALUE_SEP)).collect(Collectors.toMap(x -> Integer.parseInt(x[0]), x -> Integer.parseInt(x[1])));
    }

    private static void writeDecodedOutputToFile(byte[] bodyContent, String output) {
        File outputFile = new File(output);
        try (FileOutputStream fileOutputStream = new FileOutputStream(outputFile)) {
            fileOutputStream.write(bodyContent);
        } catch (IOException e) {
            System.out.println("Exception thrown while writing output file.");
            throw new RuntimeException(e);
        }
    }

    private static byte[] decodeFileContentToBytes(TreeNode huffmanTreeRoot, String bodyBitStream) {
        ArrayList<Byte> bodyContent = new ArrayList<>();
        TreeNode curNode = huffmanTreeRoot;
        for (char c : bodyBitStream.toCharArray()) {
            if (c == '0') {
                curNode = curNode.left;
            } else {
                curNode = curNode.right;
            }
            if (curNode.data != -1) {
                bodyContent.add((byte) curNode.data);
                curNode = huffmanTreeRoot;
            }
        }
        byte[] byteContent = new byte[bodyContent.size()];
        for (int i = 0; i < byteContent.length; i++) {
            byteContent[i] = bodyContent.get(i);
        }
        return byteContent;
    }

    public static String getBodyBitStream(ArrayList<Byte> body) {
        StringBuilder binaryString = new StringBuilder();
        for (int ind = 0; ind < body.size(); ind++) {
            byte bytes = body.get(ind);
            if (ind != body.size() - 1)
                // Pad byte with zeroes
                binaryString.append(String.format("%8s", Integer.toBinaryString(bytes & 0xFF)).replace(' ', '0'));
            else {
                // No need to pad last byte
                binaryString.append(Integer.toBinaryString(bytes & 0xFF));
            }
        }
        return binaryString.toString();
    }

    public static FileContent getCompressedFileContent(String input) {
        File inputFile = new File(input);
        StringBuilder fileHeader = new StringBuilder();
        ArrayList<Byte> body = new ArrayList<>();
        int prev = -1, cur;
        int bodyStarted = 0;
        try (FileInputStream fileReader = new FileInputStream(inputFile)) {
            while ((cur = fileReader.read()) != -1) {
                if (bodyStarted > 1) {
                    body.add((byte) cur);
                } else {
                    fileHeader.append((char) cur);
                }
                if (prev == HEADER_TERMINAL.charAt(0) && cur == HEADER_TERMINAL.charAt(1) && bodyStarted <= 1) {
                    bodyStarted += 1;
                }
                prev = cur;
            }
        } catch (IOException e) {
            System.out.println("Exception thrown while reading encoded file.");
            throw new RuntimeException(e);
        }
        return new FileContent(fileHeader.toString(), body);
    }
}
