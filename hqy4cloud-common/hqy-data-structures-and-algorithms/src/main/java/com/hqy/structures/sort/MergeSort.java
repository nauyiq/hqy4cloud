package com.hqy.structures.sort;

/**
 * 归并排序：先将一个无序的N长数组切成N个有序子序列（只有一个数据的序列认为是有序序列），然后两两合并，再将合并后的N/2（或者N/2 + 1）个子序列继续进行两两合并，以此类推得到一个完整的有序数组
 * 基于分治算法
 * 时间复杂度为O(N log2N)
 * @author qiyuan.hong
 */
public class MergeSort extends Sort {

    @Override
    public void sort() {
        recursiveMergeSort(new int[array.length], 0, array.length - 1);
    }


    /**
     * 递归的归并排序
     * @param workSpace 辅助排序的数组
     * @param lowerBound 欲归并数组段的最小下标
     * @param upperBound  欲归并数组段的最大下标
     */
    private void recursiveMergeSort(int[] workSpace, int lowerBound, int upperBound) {
        if (lowerBound != upperBound) {
            int mid = (lowerBound + upperBound) / 2;
            // 对低位段归并排序
            recursiveMergeSort(workSpace, lowerBound, mid);
            // 对高位段归并排序
            recursiveMergeSort(workSpace, mid + 1, upperBound);
            merge(workSpace, lowerBound, mid, upperBound);
            display();
        }
    }


    /**
     * 对数组array中的两段进行合并，lowerBound~mid为低位段，mid+1~upperBound为高位段
     * @param workSpace 辅助归并的数组，容纳归并后的元素
     * @param lowerBound 合并段的起始下标
     * @param mid 合并段的中点下标
     * @param upperBound 合并段的结束下标
     */
    private void merge(int[] workSpace, int lowerBound, int mid, int upperBound) {
        // 低位段的起始下标
        int lowBegin = lowerBound;
        // 高位段的起始下标
        int highBegin = mid + 1;
        // workSpace的下标指针
        int j = 0;
        // 归并的元素总数
        int n = upperBound - lowerBound + 1;

        while (lowBegin <= mid && highBegin <= upperBound) {
            if (array[lowBegin] < array[highBegin]) {
                workSpace[j++] = array[lowBegin++];
            } else {
                workSpace[j++] = array[highBegin++];
            }
        }

        while (lowBegin <= mid) {
            workSpace[j++] = array[lowBegin++];
        }

        while (highBegin <= upperBound) {
            workSpace[j++] = array[highBegin++];
        }
        // 将归并好的元素复制到array中
        for (j = 0; j < n; j++) {
            array[lowerBound++] = workSpace[j];
        }

    }


    /**
     * 归并数组A和数组B到数组C中
     * @param arrayA 待归并的数组A
     * @param sizeA 数组A的长度
     * @param arrayB 待归并的数组B
     * @param sizeB 数组B的长度
     * @param arrayC 辅助归并排序的数组
     */
    private void mergeArray(int[] arrayA, int sizeA, int[] arrayB, int sizeB, int[] arrayC) {
        //分别作为数组A、B、C的下标指针
        int i = 0, j = 0, k = 0;
        // 两个数组都不为空
        // 该循环结束后，一个数组已经完全复制到arrayC中了，另一个数组中还有元素
        while (i < sizeA && j < sizeB) {
            // 将两者较小的那个放到arrayC中
            if (arrayA[i] < arrayB[j]) {
                arrayC[k++] = arrayA[i++];
            } else {
                arrayC[k++] = arrayB[j++];
            }
        }

        // 后面的两个while循环用于处理另一个不为空的数组
        while (i < sizeA) {
            arrayC[k++] = arrayA[i++];
        }

        while (j < sizeB) {
            arrayC[k++] = arrayB[j++];
        }

        // 打印新数组中的元素
        for (int value : arrayC) {
            System.out.print(value + "\t");
        }

    }




}
