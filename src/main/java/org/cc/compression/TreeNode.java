package org.cc.compression;

public class TreeNode {
    int weight;
    int data;
    TreeNode left;
    TreeNode right;

    public TreeNode() {
        weight = -1;
        data = -1;
        left = null;
        right = null;
    }

    public TreeNode(int weight) {
        this.weight = weight;
        this.data = -1;
        left = null;
        right = null;
    }

    public TreeNode(int weight, int data) {
        this.weight = weight;
        this.data = data;
        this.left = null;
        this.right = null;
    }

    public TreeNode(int weight, TreeNode left, TreeNode right) {
        this.weight = weight;
        this.data = -1;
        this.left = left;
        this.right = right;
    }

    @Override
    public String toString() {
        return "TreeNode{" +
                "weight=" + weight +
                ", data=" + data +
                ", left=" + left +
                ", right=" + right +
                '}';
    }
}
