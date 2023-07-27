package com.hqy.cloud.arithmetic.A;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

/**
 * 高速公路休息站充电规划
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/7/3 13:19
 */
public class ExpresswayRecharge {


    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int distance = scanner.nextInt();
        int n = scanner.nextInt();

        List<Recharger> rechargers = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            rechargers.add(new Recharger(scanner.nextInt(), scanner.nextInt()));
        }

        if (distance <= 1000) {
            System.out.println(distance / 100);
        } else {
            rechargers.sort(Comparator.comparingInt(r -> r.distance));
            dp(rechargers,  distance, n);
        }


    }


    private static void dp(List<Recharger> rechargers, int distance, int n) {
        int[] dp = new int[n + 2];
        for (int i = 1; i <= n + 1; i++) {
            dp[i] = Integer.MAX_VALUE;
        }

        List<Recharger> copyRechargers = new ArrayList<>(n + 2);
        copyRechargers.add(0, new Recharger(0, 0));
        for (int i = 0; i < n; i++) {
            copyRechargers.add(i + 1, rechargers.get(i));
        }
        copyRechargers.add(n + 1, new Recharger(distance, 0));

        for (int i = 1; i <= n + 1; i++) {
            for (int j = i - 1; j >= 0; j--) {
                if (copyRechargers.get(i).distance - copyRechargers.get(j).distance > 1000) {
                    break;
                } else {
                    dp[i] = Math.min(dp[i], dp[j] + copyRechargers.get(i).wait + 1);
                }
            }

            if (dp[i] == Integer.MAX_VALUE) {
                System.out.println(-1);
                return;
            }
        }


        System.out.println(dp[n + 1] + distance / 100 - 1);

    }


    private static class Recharger {
        public int distance;
        public int wait;
        public Recharger(int distance, int wait) {
            this.distance = distance;
            this.wait = wait;
        }
    }

}
