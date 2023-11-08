package com.hqy.cloud.arithmetic.B;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * 矩阵稀疏扫描
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/6/26 9:49
 */
public class SparseMatrix {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int m = scanner.nextInt();
        int n = scanner.nextInt();

        int[][] matrix = new int[m][n];

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                matrix[i][j] = scanner.nextInt();
            }
        }

        //反转数组.
        int[][] reverseMatrix = new int[n][m];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                reverseMatrix[i][j] = matrix[j][i];
            }
        }

        int rowHalf = n / 2;
        int lightHalf = m / 2;

        int x = dfs(matrix, rowHalf);
        int y = dfs(reverseMatrix, lightHalf);
        System.out.println(x);
        System.out.println(y);
    }


    public static int dfs(int[][] data, int half) {
        int result = 0;
        List<Integer> list = new ArrayList<>();
        for (int[] ints : data) {
            list.clear();
            for (int i : ints) {
                if (i == 0) {
                    list.add(i);
                } else {
                    list.clear();
                }

                if (list.size() > half) {
                    result += 1;
                    list.clear();
                }
            }
        }
        return result;
    }



}
