package com.hqy.cloud.arithmetic.A;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/7/14 14:49
 */
public class XinHao {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        int m = scanner.nextInt();
        int[][] matrix = new int[n][m];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                matrix[i][j] = scanner.nextInt();
            }
        }

        int[][] result = new int[n][m];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                bfs(matrix, i, j, n, m, result);
            }
        }

        System.out.println(n + " " + m);
        String output = "";
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                output = output + " " + result[i][j];
            }
        }
        System.out.println(output.trim());

    }


    private static void bfs(int[][] matrix, int x, int y, int n, int m, int[][] result) {
        int height = matrix[x][y];
        if (y > 0) {
            // 往西搜索
            List<Integer> temp = new ArrayList<>();
            for (int i = y - 1; i >= 0; i--) {
                int right = matrix[x][i];
                if (check(temp, right, height)) {
                    result[x][y] = result[x][y] + 1;
                }
                temp.add(right);
            }
        }

        if (x > 0) {
            // 往北搜索
            List<Integer> temp = new ArrayList<>();
            for (int i = x - 1; i >= 0; i--) {
                int button = matrix[i][y];
                if (check(temp, button, height)) {
                    result[x][y] = result[x][y] + 1;
                }
                temp.add(button);
            }
        }

    }
    
    private static boolean check(List<Integer> heights, int data, int value) {
        if (heights.isEmpty()) {
            return true;
        }
        for (Integer height : heights) {
            if (height >= data || height >= value) {
                return false;
            }
        }
        return true;
    }
    
}
