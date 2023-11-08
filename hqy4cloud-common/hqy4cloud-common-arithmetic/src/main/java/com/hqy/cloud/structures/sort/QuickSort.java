package com.hqy.cloud.structures.sort;

/**
 * 快速排序也是基于分治算法
 * 选择一个基准元素，通常选择第一个元素或者最后一个元素；
 * 通过一趟排序讲待排序的记录分割成独立的两部分，其中一部分记录的元素值均比基准元素值小。另一部分记录的 元素值比基准值大；
 * 此时基准元素在其排好序后的正确位置；
 * 然后分别对这两部分记录用同样的方法继续进行排序，直到整个序列有序。
 * 时间复杂度 O(nlog2n)
 * @author qiyuan.hong
 */
public class QuickSort extends Sort {

    @Override
    public void sort() {
        recursiveQuickSort(0, array.length - 1);
    }


    private void recursiveQuickSort(int low, int high) {

        if (low < high) {
            //TODO 选定第一个元素为枢纽实现起来确实很简单，但是当它为最大值或最小值时，快速排序的效率会严重降低。
            // 假如选中的元素为数组的中值，自然是最好的选择，但是却要遍历整个数组来确定中值，这个过程可能比排序花费的时间还长，得不偿失。
            // 折衷的方法是找到数组中的第一个、最后一个以及处于中间位置的元素，选出三者的中值作为枢纽，既避免了枢纽是最值的情况，也不会像在全部元素中寻找中值那样费时间。
            // 这种方法被称为“三项数据取中”(median-of-three)。

            //以第一个元素为基准
            int pivot = array[low];
            // 对数组进行划分，比pivot小的元素在低位段，比pivot大的元素在高位段
            int partition = partition(low, high, pivot);
            display();
            // 对划分后的低位段进行快速排序
            recursiveQuickSort(low, partition - 1);
            // 对划分后的高位段进行快速排序
            recursiveQuickSort(partition + 1, high);
        }
    }


    /**
     * 以pivot为基准对下标low到high的数组进行划分
     * @param low 数组段的最小下标
     * @param high 数组段的最大下标
     * @param pivot 基本点的值
     * @return 划分完成后基准元素所在位置的下标
     */
    private int partition(int low, int high, int pivot) {

        while (low < high) {

            while (low < high && array[high] >= pivot) {
                high--;
            }
            swap(low, high);

            while (low < high && array[low] <= pivot) {
                low++;
            }
            swap(low, high);
        }
        return low;

    }

}
