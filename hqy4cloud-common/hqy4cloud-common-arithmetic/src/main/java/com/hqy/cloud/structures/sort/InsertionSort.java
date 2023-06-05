package com.hqy.cloud.structures.sort;

/**
 * 在要排序的一组数中，假设前面(n-1)[n>=2] 个数已经是排好顺序的，现在要把第n个数找到相应位置并插入，使得这n个数也是排好顺序的。如此反复循环，直到全部排好顺序。
 * 时间复杂度为O(n^2) , 空间复杂度O(1)
 * @author qiyuan.hong
 */
public class InsertionSort extends Sort {

    @Override
    public void sort() {

        int len = array.length;
        int counter = 1;

        for (int i = 1; i < len; i ++) {
            //存储待排序的元素
            int temp = array[i];
            //与待排序元素做比较的元素下标
            int insertPoint = i - 1;
            //当前元素比待比较元素大
            while (insertPoint >= 0 && array[insertPoint] > temp) {
                //当前元素后移一位
                array[insertPoint + 1] = array[insertPoint];
                insertPoint--;
            }
            //找到了插入位置， 插入待排序元素
            array[insertPoint + 1] = temp;
            System.out.print("第" + counter + "轮排序结果：");
            display();
            counter++;
        }

    }
}
