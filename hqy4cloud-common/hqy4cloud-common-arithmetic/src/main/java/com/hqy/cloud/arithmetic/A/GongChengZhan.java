package com.hqy.cloud.arithmetic.A;

import java.util.*;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/7/4 13:51
 */
public class GongChengZhan {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        //大炮种数
        int n = scanner.nextInt();
        //火药数目.
        int m = scanner.nextInt();
        //攻城时间
        int t = scanner.nextInt();

        int[] a = new int[n];
        int[] b = new int[n];
        int[] c = new int[n];

        for (int i = 0; i < n; i++) {
            a[i] = scanner.nextInt();
            b[i] = scanner.nextInt();
            c[i] = scanner.nextInt();
        }
        //dp[i][j]表示在前i种大炮中使用j个火药的情况下，能够给予城池的最大打击。
        int[][] dp = new int[n + 1][m + 1];

        for (int i = 1; i <= n ; i++) {
            for (int j = 1; j <= m; j++) {
                int maxAttache = Math.min(j / b[i - 1], t / c[i - 1]);
                for (int k = 0; k <= maxAttache; k++) {
                    dp[i][j] = Math.max(dp[i][j], dp[i - 1][j - k * b[i - 1]] + k * a[i - 1]);
                }
            }

        }

        System.out.println(dp[n][m]);







    }



}
