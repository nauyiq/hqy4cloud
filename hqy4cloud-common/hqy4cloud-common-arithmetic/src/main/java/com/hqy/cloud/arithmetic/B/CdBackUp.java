package com.hqy.cloud.arithmetic.B;

import java.util.Arrays;
import java.util.Scanner;

/**
 * 光盘 数据最节约的备份方法
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/6/16 16:46
 */
public class CdBackUp {

    static int[] inputFiles;
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        String[] split = input.split(",");
        if (split.length == 0) {
            return;
        }
        inputFiles = Arrays.stream(split).mapToInt(Integer::valueOf).sorted().toArray();

        int left = 0;
        int right = inputFiles.length - 1;


        while (left < right) {
            int mid = (left + right) / 2;
            if (action(mid)) {
                right = mid;
            } else {
                left = mid + 1;
            }
        }

        System.out.println(left);
    }



    public static boolean action(int mid) {
        int[] nums = new int[mid];
        for (int i = 0; i < mid; i++) {
            nums[i] = 500;
        }

        for (int i = inputFiles.length - 1; i > 0; i--) {
            int num = inputFiles[i];
            Arrays.sort(nums);
            if (nums[mid - 1] >= num) {
                nums[mid -1] -= num;
            } else {
                return false;
            }
        }

        return true;
    }




}
