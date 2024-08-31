package org.cc.compression;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.cc.compression.Encoder.createHuffmanEncodingHeader;
import static org.cc.compression.Utils.*;

public class HuffmanTest {

    Map<Integer, Integer> getSampleHuffmanTree() {
        return Map.of(
                (int) 'C', 32,
                (int) 'D', 42,
                (int) 'E', 120,
                (int) 'K', 7,
                (int) 'L', 42,
                (int) 'M', 24,
                (int) 'U', 37,
                (int) 'Z', 2);
    }


    @Test
    public void testFreq() {
        Map<Integer, Integer> frequency = getFrequencyOfText("src/test/resources/org/cc/compression/135-0.txt");
        Assertions.assertEquals(223000, frequency.get((int) 't'));
        Assertions.assertEquals(333, frequency.get((int) 'X'));
    }

    @Test
    public void testBuildHuffmanTree() {
        Map<Integer, Integer> freqMap = getSampleHuffmanTree();

        TreeNode treeNode = buildHuffmanPrefixTree(freqMap);
        Assertions.assertEquals('U', treeNode.right.left.left.data);
        Assertions.assertEquals('D', treeNode.right.left.right.data);
        Assertions.assertEquals(42, treeNode.right.left.right.weight);
        Assertions.assertEquals(107, treeNode.right.right.weight);
    }

    @Test
    public void testBuildHuffmanCodes() {
        Map<Integer, Integer> freqMap = getSampleHuffmanTree();

        TreeNode treeNode = buildHuffmanPrefixTree(freqMap);
        Map<Integer, String> huffmanCodes = buildHuffmanCodes(treeNode);

        Assertions.assertEquals("101", huffmanCodes.get((int) 'D'));
        Assertions.assertEquals("1110", huffmanCodes.get((int) 'C'));
        Assertions.assertEquals("11111", huffmanCodes.get((int) 'M'));
    }

    @Test
    public void testBuildHuffmanHeader() {
        String expectedHeader = "HD137=>1##10=>17##13=>17##32=>39##35=>1##169=>1##42=>6##44=>2##45=>1##46=>1##48=>2##49=>3##50=>3##51=>1##52=>1##53=>1##56=>1##57=>2##58=>8##65=>3##66=>5##195=>2##67=>3##68=>2##69=>8##70=>4##71=>2##72=>3##73=>2##74=>3##75=>1##76=>4##77=>4##78=>1##79=>4##80=>2##82=>5##83=>4##84=>8##85=>2##86=>2##87=>1##91=>2##93=>2##97=>15##98=>3##99=>5##100=>10##101=>22##103=>7##104=>4##105=>10##107=>1##108=>10##109=>2##110=>8##111=>14##112=>3##114=>10##115=>12##116=>12##117=>8##118=>2##121=>4HD";
        Map<Integer, Integer> frequency = getFrequencyOfText("src/test/resources/org/cc/compression/small.txt");
        String compressionHeader = createHuffmanEncodingHeader(frequency);
        Assertions.assertEquals(expectedHeader, compressionHeader);
    }
}
