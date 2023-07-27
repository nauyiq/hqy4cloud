package com.hqy.cloud.arithmetic.A;

import java.util.Arrays;
import java.util.Scanner;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/6/29 14:02
 */
public class SmartDeliveryMan {


    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        int m = scanner.nextInt();

        //采用动态规划解决问题, dp[i] 表示都第n层到达第i层所需要的最短时间.
        int[] dp = new int[2 * m + 1];
        // 初始化 用Integer.MAX_VALUE 表示所有楼层都不可达. 并且到达目前所在的n层的时间0
        Arrays.fill(dp, Integer.MAX_VALUE);
        dp[n] = 0;

        for (int i = n; i <= m; i++) {
            if (i + 1 <= m) {
                //选择步行向上
                dp[i + 1] = Math.min(dp[i + 1], dp[i] + 1);
            }
            if (i - 1 <= n) {
                //选择步行向下
                dp[i - 1] = Math.min(dp[i - 1], dp[i] + 1);
            }
            if (2 * i <= dp.length) {
                //选择坐电梯
                dp[2 * i] = Math.min(dp[2 * i], dp[i] + 1);
            }

        }

        System.out.println(dp[m]);


    }



    
}
