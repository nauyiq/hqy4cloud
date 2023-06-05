package com.hqy.cloud.structures.sort;

/**
 * 冒泡排序: 每当俩相邻的数比较后发现他们的排序与排序的要求相反时，就将他们交换。每次遍历都可确定一个最大值放到待排数组的末尾，下次遍历，对该最大值以及它之后的元素不再排序（已经排好）。
 * 冒泡排序的效率很低
 * 时间复杂度为O(n^2) , 空间复杂度O(1)
 * @author qiyuan.hong
 */
public class BubbleSort extends Sort {

    @Override
    public void sort() {
        int temp;
        int length = array.length;

        //FIXME 最简单的冒牌排序, 无任何优化的
        /*for (int i = 0; i < length - 1; i++) {
            for (int j = 1; j < length - i; j++) {
                if (array[j - 1] > array[j]) {
                    temp = array[j - 1];
                    array[j - 1] = array[j];
                    array[j] = temp;
                }
            }
            System.out.print("第" + (i + 1) + "轮排序结果：");
            display();
        }*/

        //FIXME 改进一： 加入标志性变量exchange， 表示某一趟排序过程已经没有数据交换
        /*for (int i = 0; i < length - 1; i++) {
            boolean exchange = false;
            for (int j = 1; j < length - i; j++) {
                if (array[j - 1] > array[j]) {
                    temp = array[j - 1];
                    array[j - 1] = array[j];
                    array[j] = temp;

                    if (!exchange) {
                        exchange = true;
                    }
                }
            }
            System.out.print("第" + (i + 1) + "轮排序结果：");
            display();

            if (!exchange) break; // 如果上一轮没有发生交换数据，证明已经是有序的了，结束排序
        }*/

        /**
         * FIXME 改进二： 设置一个pos指针，pos后面的数据在上一轮排序中没有发生交换，下一轮排序时，就对pos之后的数据不再比较。
         */
       /* int counter = 1;
        int endPoint = array.length - 1; //表示最后一个需要比较元素的下标

        while (endPoint > 0) {
            int pos = 1; //指针变量
            for (int j = 1; j<= endPoint; j++) {
                if (array[j-1] > array[j]) {
                    temp = array[j - 1];
                    array[j - 1] = array[j];
                    array[j] = temp;

                    pos = j; // 下标为j的元素与下标为j-1的元素发生了数据交换
                }
            }
            endPoint = pos - 1; // 下一轮排序时只对下标小于pos的元素排序，下标大于等于pos的元素已经排好
            System.out.print("第" + counter + "轮排序结果：");
            display();
            counter++;
        }*/


        /**
         * FIXME 改进三：传统的冒泡算法每次排序只确定了最大值，我们可以在每次循环之中进行正反两次冒泡，
         * 分别找到最大值和最小值，如此可使排序的轮数减少一半
         */
        int low = 0;
        int high = array.length - 1;
        int counter = 1;

        while (low < high) {
            // 正向冒泡，确定最大值
            for (int i = low; i < high; ++i) {
                // 如果前一位大于后一位，交换位置
                if (array[i] > array[i + 1]) {
                    temp = array[i];
                    array[i] = array[i + 1];
                    array[i + 1] = temp;
                }
            }
            --high;
            // 反向冒泡，确定最小值
            for (int j = high; j > low; --j) {
                // 如果前一位大于后一位，交换位置
                if (array[j] < array[j - 1]) {
                    temp = array[j];
                    array[j] = array[j - 1];
                    array[j - 1] = temp;
                }
            }
            ++low;

            System.out.print("第" + counter + "轮排序结果：");
            display();
            counter++;
        }

    }

}
