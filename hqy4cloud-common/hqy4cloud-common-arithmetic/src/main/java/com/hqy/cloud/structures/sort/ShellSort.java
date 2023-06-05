package com.hqy.cloud.structures.sort;

/**
 * 希尔排序是基于插入排序的，又叫缩小增量排序
 * 希尔排序通过加大插入排序时元素之间的间隔，并对这些间隔的元素进行插入排序，从而使数据能大跨度地移动。数据项之间的间隔被称为增量，习惯上还用h表示。
 * 因此元素之间的间隔非常重要，即增量。 这里使用增量算法 h = 3 * h + 1
 * 希尔排序的时间复杂度 O(N*(logN)2)。
 * @author qiyuan.hong
 */
public class ShellSort extends Sort {

    private static final int INTERVAL = 3;

    @Override
    public void sort() {
        int len = array.length;
        int counter = 1;
        int h = 1;
        //确定第一轮排序时的间隔
        while (INTERVAL * h + 1 < len) {
            h = 3 * h + 1;
        }

        // 对间隔为h的元素进行插入排序
        while (h > 0) {
            for (int i = 0;  i< h; i++) {
                shellInsertSort(i, h);
            }
            // 下一轮排序的间隔
            h = (h - 1) / INTERVAL;
            System.out.print("第" + counter + "轮排序结果：");
            display();
            counter++;
        }

    }

    /**
     * 希尔排序内部使用的插入排序
     * 需要进行插入排序的元素为array[beginIndex]、 array[beginIndex + increment]、 array[beginIndex + 2 * increment] 、array[beginIndex + 3 * increment]、 array[beginIndex + ... * increment]
     * @param beginIndex 其实下标
     * @param increment 变量
     */
    private void shellInsertSort(int beginIndex, int increment) {
        //欲插入元素的下标
        int targetIndex = beginIndex + increment;

        while (targetIndex < array.length) {
            int temp = array[targetIndex];
            //前一个元素的下标， 间隔为increment
            int previousIndex = targetIndex - increment;
            while (previousIndex >= 0 && array[previousIndex] > temp) {
                //比欲插入数据项大的元素后移一位
                array[previousIndex + increment] = array[previousIndex];
                previousIndex = previousIndex - increment;
            }
            //插入到合适的位置
            array[previousIndex + increment] = temp;
            //插入下一个元素
            targetIndex = targetIndex + increment;
        }
    }

}
