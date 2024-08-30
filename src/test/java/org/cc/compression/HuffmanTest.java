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

    @Test
    public void testBuildHuffmanHeader() {
        String expectedHeader = "HEAD\n->17||\r->17|| ->39||#->1||*->6||,->2||-->1||.->1||0->2||1->3||2->3||3->1||4->1||5->1||8->1||9->2||:->8||A->3||B->5||C->3||D->2||E->8||F->4||G->2||H->3||I->2||É->1||J->3||K->1||L->4||M->4||N->1||O->4||P->2||R->5||S->4||T->8||U->2||V->2||W->1||[->2||]->2||a->15||b->3||c->5||d->10||e->22||g->7||h->4||i->10||é->1||k->1||l->10||m->2||n->8||o->14||p->3||r->10||s->12||t->12||u->8||v->2||y->4HEAD";
        Map<Character, Integer> frequency = Huffman.getFrequencyOfText("src/test/java/org/cc/compression/small.txt");
        String compressionHeader = Huffman.getHuffmanCompressionHeader(frequency);
        Assertions.assertEquals(expectedHeader, compressionHeader);
    }
}
