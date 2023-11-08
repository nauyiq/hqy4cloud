package com.hqy.cloud.arithmetic.A;

import java.util.Arrays;
import java.util.Scanner;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/7/4 9:48
 */
public class ArrayLimiterNumber {

    static int result = 0;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        int m = scanner.nextInt();

        int[] arrays = new int[n];
        int sum = 0;
        for (int i = 0; i < n; i++) {
            int num = scanner.nextInt();
            arrays[i] = num;
            sum += num;
        }

        if (sum <= m) {
            System.out.println(-1);
            return;
        }

        Arrays.sort(arrays);
        find(arrays, 0, arrays[n - 1], m);
        System.out.println(result);
    }



    private static void find(int[] arrays, int left, int right, int total) {
        while (left <= right) {
            int mid = (left + right) >> 1;
            if (check(arrays, mid, total)) {
                if (mid >= 1) {
                    ++result;
                }
                find(arrays, left, mid - 1, total);
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }
    }

    private static boolean check(int[] arrays, int mid, int total) {
        int sum = 0;
        for (int array : arrays) {
            sum += Math.min(array, mid);
        }
        return sum <= total;
    }


}
