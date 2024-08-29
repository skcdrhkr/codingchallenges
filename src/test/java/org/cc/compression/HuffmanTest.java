package org.cc.compression;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;

public class HuffmanTest {

    Map<Character, Integer> getSampleHuffmanTree() {
        return Map.of(
                'C', 32,
                'D', 42,
                'E', 120,
                'K', 7,
                'L', 42,
                'M', 24,
                'U', 37,
                'Z', 2);
    }


    @Test
    public void testFreq() {
        Map<Character, Integer> frequency = Huffman.getFrequencyOfText("src/test/java/org/cc/compression/135-0.txt");
        Assertions.assertEquals(223000, frequency.get('t'));
        Assertions.assertEquals(333, frequency.get('X'));
    }

    @Test
    public void testBuildHuffmanTree() {
        Map<Character, Integer> freqMap = getSampleHuffmanTree();

        TreeNode treeNode = Huffman.buildHuffmanTree(freqMap);
        Assertions.assertEquals('U', treeNode.right.left.left.data);
        Assertions.assertEquals('D', treeNode.right.left.right.data);
        Assertions.assertEquals(42, treeNode.right.left.right.weight);
        Assertions.assertEquals(107, treeNode.right.right.weight);
    }

    @Test
    public void testBuildHuffmanCodes() {
        Map<Character, Integer> freqMap = getSampleHuffmanTree();

        TreeNode treeNode = Huffman.buildHuffmanTree(freqMap);
        Map<Character, String> huffmanCodes = Huffman.buildHuffmanCodes(treeNode);

        Assertions.assertEquals("101", huffmanCodes.get('D'));
        Assertions.assertEquals("1110", huffmanCodes.get('C'));
        Assertions.assertEquals("11111", huffmanCodes.get('M'));

    }
}
