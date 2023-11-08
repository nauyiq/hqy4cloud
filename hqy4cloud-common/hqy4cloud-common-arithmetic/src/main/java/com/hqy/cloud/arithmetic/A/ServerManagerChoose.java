package com.hqy.cloud.arithmetic.A;

import java.util.*;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/7/13 10:32
 */
public class ServerManagerChoose {
    static int result = Integer.MAX_VALUE;
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        int[][] matrix = new int[n][2];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < 2; j++) {
                matrix[i][j] = scanner.nextInt();
            }
        }

        if (n <= 1) {
            System.out.println(0);
        } else {
            dfs(matrix, 0, 0);
            System.out.println(result);
        }



    }

    private static void dfs(int[][] matrix, int index, int value) {
        if (index == matrix.length - 1) {
            return;
        }

        int[] current = matrix[index];
        int[] next = matrix[index + 1];

        int temp = 0;
        if (current[1] == next[0]) {
            temp = next[0];
        } else if (current[1] == next[1]) {
            temp = next[1];
        } else if (current[1] > next[0]) {
            temp = current[1];
        } else {
            if ((current[1] + next[0]) % 2 == 0) {
                temp = (current[1] + next[0]) / 2;
            } else {
                temp = (current[1] + next[0]) / 2 + 1;
            }
        }

        int sum = 0;
        for (int[] ints : matrix) {
            int x = ints[0];
            int y = ints[1];
            if (x > temp) {
                sum += x - temp;
            } else if (y < temp) {
                sum += temp - y;
            }
        }

        if (sum < result) {
            result = sum;
        }

        dfs(matrix, ++index, temp);

    }



}
