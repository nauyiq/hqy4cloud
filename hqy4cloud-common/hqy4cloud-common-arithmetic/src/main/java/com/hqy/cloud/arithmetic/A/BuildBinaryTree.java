package com.hqy.cloud.arithmetic.A;

import java.util.*;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/7/5 16:23
 */
public class BuildBinaryTree {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String[] split = scanner.nextLine().trim().replace("[", "").replace("]", "").split(",");
        if (split.length == 0) {
            System.out.println("[-1]");
            return;
        }
        int flag = 0;
        int n = split.length / 2;
        int[][] operators = new int[n][2];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < 2; j++) {
                operators[i][j] = Integer.parseInt(split[flag].trim());
                flag++;
            }
        }
        //头结点在第0层
        Node header = new Node(-1);
        //树结构.
        List<List<Node>> binaryTree = new ArrayList<>();
        //添加头结点.
        binaryTree.add(Collections.singletonList(header));

        for (int i = 0; i < n; i++) {
            int height = operators[i][0];
            int index = operators[i][1];
            Node node = new Node(i);

            if (height + 1 >= binaryTree.size()) {
                binaryTree.add(new ArrayList<>());
            }
            binaryTree.get(height + 1).add(node);

            Node parentNode = binaryTree.get(height).get(index);
            if (parentNode.left == null) {
                parentNode.left = node;
            } else if (parentNode.right == null) {
                parentNode.right = node;
            }
        }

        LinkedList<Integer> result = new LinkedList<>();
        LinkedList<Node> iteratorTree = new LinkedList<>();
        iteratorTree.add(binaryTree.get(0).get(0));

        while (!iteratorTree.isEmpty()) {
            Node node = iteratorTree.removeFirst();
            if (node == null) {
                result.add(null);
            } else {
                result.add(node.data);
                iteratorTree.add(node.left);
                iteratorTree.add(node.right);
            }
        }

        while (true) {
            if (result.getLast() != null) {
                break;
            } else {
                result.removeLast();
            }
        }

        StringBuilder output_str = new StringBuilder("[");
        for (int i = 0; i < result.size(); i++) {
            if (result.get(i) == null) {
                output_str.append("null");
            } else {
                output_str.append(result.get(i));
            }

            if (i != result.size() - 1) {
                output_str.append(",");
            }
        }
        output_str.append("]");

        System.out.println(output_str);
    }


    private static class Node {
        public int data;
        public Node left;
        public Node right;

        public Node(int data) {
            this.data = data;
        }

        public Node(int data, Node left, Node right) {
            this.data = data;
            this.left = left;
            this.right = right;
        }

        public void setLeft(Node left) {
            this.left = left;
        }

        public void setRight(Node right) {
            this.right = right;
        }
    }

}
