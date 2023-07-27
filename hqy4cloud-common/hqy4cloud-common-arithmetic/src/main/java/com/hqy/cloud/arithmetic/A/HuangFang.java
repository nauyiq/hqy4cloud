package com.hqy.cloud.arithmetic.A;

import java.util.*;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/6/28 16:38
 */
public class HuangFang {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        int[][] matrix = new int[n][n];
        int[] xSum = new int[n];
//        int[] ySum = new int[n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                matrix[i][j] = scanner.nextInt();
                xSum[i] += matrix[i][j];
//                ySum[j] += matrix[i][j];
            }
        }

        List<Integer> error_x_index = new ArrayList<>(2);
//        List<Integer> error_y_index = new ArrayList<>(2);
        int target = n * (n * n + 1) / 2;

        for (int i = 0; i < n; i++) {
            if (xSum[i] != target) {
                error_x_index.add(i);
            }

           /* if (ySum[i] != target) {
                error_y_index.add(i);
            }*/
        }


        if (error_x_index.size() == 2) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    Integer x1 = error_x_index.get(0);
                    Integer x2 = error_x_index.get(1);
                    if (xSum[x1] - matrix[x1][i] + matrix[x2][j] == target) {
                        System.out.println(x1 + 1 + " " + (i + 1) + " " + matrix[x2][j]);
                        System.out.println(x2 + 1 + " " + (j + 1) + " " + matrix[x1][i]);
                    }
                }
            }
        }














    }




}
