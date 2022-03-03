package com.hqy.structures.sort;

/**
 * 堆排序是一种树形选择排序，是对直接选择排序的改进。
 * 1.堆中某个节点的值总是不大于或不小于其父节点的值；
 * 2.堆总是一棵完全二叉树（Complete Binary Tree）。
 * 初始时把要排序的n个数看作是一棵顺序存储的完全二叉树，调整它们的存储顺序，使之成为一个堆，将堆顶元素输出，得到n 个元素中最小（最大）的元素，这时堆的根节点的数最小（或者最大）。
 * 然后对前面(n-1)个元素重新调整使之成为堆，输出堆顶元素，得到n 个元素中次小(或次大)的元素。依次类推，直到只有两个节点的堆，并对它们作交换，最后得到有n个节点的有序序列。这个过程就称为堆排序。
 * 堆排序的时间复杂度为O(nlogn)。由于堆排序对原始记录的排序状态并不敏感，因此它无论是最好、最坏和平均时间复杂度均为O(nlogn)。这在性能上显然要远远好过于冒泡、简单选择、直接插入的O(n2)的时间复杂度了
 * @author qiyuan.hong
 */
public class HeapSort extends Sort {

    @Override
    public void sort() {
        buildHeap();
        System.out.println("建堆：");
        printTree(array.length);

        int lastIndex = array.length - 1;

        while (lastIndex > 0) {
            // 取出堆顶元素，将堆底放入堆顶。其实就是交换下标为0与lastIndex的数据
            swap(0, lastIndex);
            // 只有一个元素时就不用调整堆了，排序结束
            if (--lastIndex == 0) {
                break;
            }
            // 调整堆
            adjustHeap(0, lastIndex);
            System.out.println("调整堆：");
            printTree(lastIndex + 1);
        }
    }


    /**
     * 用数组中的元素建堆
     */
    private void buildHeap() {
        int lastIndex = array.length - 1;
        for (int i = (lastIndex - 1) / 2; i >= 0; i--) {
            // (lastIndex-1)/2就是最后一个元素的根节点的下标，依次调整每棵子树
            // 调整以下标i的元素为根的子树
            adjustHeap(i, lastIndex);
        }
    }


    /**
     * 调整以下标是rootIndex的元素为根的子树
     * @param rootIndex  根的下标
     * @param lastIndex 堆中最后一个元素的下标
     */
    private void adjustHeap(int rootIndex, int lastIndex) {
        int biggerIndex = rootIndex;
        int leftChildIndex = 2 * rootIndex + 1;
        int rightChildIndex = 2 * rootIndex + 2;
        // 存在右子节点，则必存在左子节点
        if (rightChildIndex <= lastIndex) {
            // 子节点中存在比根更大的元素
            if (array[rootIndex] < array[leftChildIndex] || array[rootIndex] < array[rightChildIndex]) {
                biggerIndex = array[leftChildIndex] < array[rightChildIndex] ? rightChildIndex : leftChildIndex;
            }
        } else if (leftChildIndex <= lastIndex) {
            // 只存在左子节点
            // 左子节点更大
            if (array[leftChildIndex] > array[rootIndex]) {
                biggerIndex = leftChildIndex;
            }
        }
        // 找到了比根更大的子节点
        if (biggerIndex != rootIndex) {
            swap(rootIndex, biggerIndex);

            // 交换位置后可能会破坏子树，将焦点转向交换了位置的子节点，调整以它为根的子树
            adjustHeap(biggerIndex, lastIndex);
        }

    }




    /**
     * 将数组按照完全二叉树的形式打印出来
     * @param len 需要打印的数组长度
     */
    private void printTree(int len) {
        // 树的层数
        int layers = (int) Math.floor(Math.log(len) / Math.log(2)) + 1;
        int endSpacing = (int) Math.pow(2, layers) - 1;
        int spacing;
        int numberOfThisLayer;
        // 从第一层开始，逐层打印
        for (int i = 1; i <= layers; i++) {
            // 每层打印之前需要打印的空格数
            endSpacing = endSpacing / 2;
            // 元素之间应该打印的空格数
            spacing = 2 * endSpacing + 1;
            // 该层要打印的元素总数
            numberOfThisLayer = (int) Math.pow(2, i - 1);

            int j;
            for (j = 0; j < endSpacing; j++) {
                System.out.print("  ");
            }
            // 该层第一个元素对应的数组下标
            int beginIndex = (int) Math.pow(2, i - 1) - 1;

            for (j = 1; j <= numberOfThisLayer; j++) {
                System.out.print(array[beginIndex++] + " ");
                // 打印元素之间的空格
                for (int k = 0; k < spacing; k++) {
                    System.out.print("  ");
                }
                // 已打印到最后一个元素
                if (beginIndex == len) {
                    break;
                }
            }
            System.out.println();
        }
        System.out.println();
    }

}
