package com.hqy.structures.sort;

/**
 * @author qy.hong
 * @date 2022-02-15 22:19
 */
public abstract class Sort {

    public static int[] array = {21, 9, 10, 2, 1 , 3, 7, 5, 8, 51, 12, 6, 17};

    public void display() {
        for (int value : array) {
            System.out.print(value + "\t");
        }
        System.out.println();
    }

    /**
     * 二分查找法
     * @param lowerBound 查找段的最小下标
     * @param upperBound 查找段的最大下标
     * @param target 目标元素
     * @return 目标元素应该插入位置的下标
     */
    public int binarySearch(int lowerBound, int upperBound, int target) {
        int curIndex;
        while (lowerBound < upperBound) {
            curIndex = (lowerBound + upperBound) / 2;
            if (array[curIndex] > target) {
                upperBound = curIndex - 1;
            } else {
                lowerBound = curIndex + 1;
            }
        }
        return lowerBound;
    }

    /**
     * 交换数组的两个元素数组
     * @param low  欲交换元素的低位下标
     * @param high 欲交换元素的高位下标
     */
    public void swap(int low, int high) {
        int temp = array[low];
        array[low] = array[high];
        array[high] = temp;
    }

    /**
     * 具体排序实现交给子类.
     */
    public abstract void sort();



}
