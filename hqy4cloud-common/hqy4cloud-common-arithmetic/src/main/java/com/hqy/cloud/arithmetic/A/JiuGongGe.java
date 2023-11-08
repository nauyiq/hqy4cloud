package com.hqy.cloud.arithmetic.A;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/7/14 10:40
 */
public class JiuGongGe {

    static List<int[]> result = new ArrayList<>();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int[] numbers = new int[9];
        for (int i = 0; i < 9; i++) {
            numbers[i] = scanner.nextInt();
        }

        Arrays.sort(numbers);

        sort(numbers, 0, 8);

        result.sort((r1, r2) -> {
            for (int i = 0; i < 9; i++) {
                if (r1[i] != r2[i]) {
                    return r1[i] - r2[i];
                }
            }
            return 0;
        });

        for (int[] ints : result) {
            String output = "";
            for (int i : ints) {
                output = output + " " + i;
            }
            System.out.println(output);
        }


    }

    private static void sort(int[] numbers, int i, int j) {
        if (i == j) {
            if (check(numbers)) {
                result.add(Arrays.copyOf(numbers, 9));
            }
        } else {
            for (int k = i; k <= j; k++) {
                swap(numbers, i, k);
                sort(numbers, i + 1, j);
                swap(numbers, i, k);
            }
        }
    }

    private static boolean check(int[] numbers) {
        int value = numbers[0] * numbers[1] * numbers[2];

        // 校验行
        if (numbers[3] * numbers[4] * numbers[5] != value || numbers[6] * numbers[7] * numbers[8] != value) {
            return false;
        }

        //校验列
        if (numbers[0] * numbers[3] * numbers[6] != value || numbers[1] * numbers[4] * numbers[7] != value || numbers[2] * numbers[5] * numbers[8] != value) {
            return false;
        }

        //校验对角线
        if (numbers[0] * numbers[4] * numbers[8] != value || numbers[2] * numbers[4] * numbers[6] != value) {
            return false;
        }

        return true;

    }

    private static void swap(int[] numbers, int i, int k) {
        int temp = numbers[i];
        numbers[i] = numbers[k];
        numbers[k] = temp;
    }


}
