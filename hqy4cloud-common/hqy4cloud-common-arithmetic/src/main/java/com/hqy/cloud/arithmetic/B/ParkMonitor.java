package com.hqy.cloud.arithmetic.B;

import java.util.Scanner;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/6/28 10:36
 */
public class ParkMonitor {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int m = scanner.nextInt();
        int n = scanner.nextInt();
        if (m < 1 || n < 1 || m > 20 || n > 20) {
            return;
        }

        int[][] parkMatrix = new int[m][n];

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                int status = scanner.nextInt();
                if (status != 0 && status != 1) {
                    return;
                }
                parkMatrix[i][j] = status;
            }
        }

        int result = findNeedMonitor(parkMatrix, m, n);
        System.out.println(result);
    }

    private static int findNeedMonitor(int[][] parkMatrix, int m, int n) {
        int[][] monitors = new int[m][n];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                int status = parkMatrix[i][j];
                if (status == 1) {
                    //当前位置的监视器肯定需要打开.
                    monitors[i][j] = 1;
                    //遍历当前位置的上下左右位置.
                    dfs(monitors, i, j, m, n);
                }

            }
        }
        int result = 0;
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (monitors[i][j] == 1) {
                    result++;
                }
            }
        }

        return result;
    }

    private static void dfs(int[][] monitors, int i, int j, int m, int n) {
        if (i - 1 >= 0) {
            monitors[i - 1][j] = 1;
        }

        if (i + 1 < m) {
            monitors[i + 1][j] = 1;
        }

        if (j - 1 >= 0) {
            monitors[i][j - 1] = 1;
        }

        if (j + 1 < n) {
            monitors[i][j + 1] = 1;
        }

    }

}
