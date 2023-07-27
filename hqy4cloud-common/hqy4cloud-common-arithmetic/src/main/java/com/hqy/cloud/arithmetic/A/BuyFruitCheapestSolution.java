package com.hqy.cloud.arithmetic.A;

import java.util.*;

/**
 * 购买水果最便宜方案
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/6/29 15:20
 */
public class BuyFruitCheapestSolution {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        // 总小时数
        int hour = scanner.nextInt();
        // 超市数
        int supermarket = scanner.nextInt();
        // 折扣情况
        List<Supermarket> supermarkets = new ArrayList<>(supermarket);

        for (int i = 0; i < supermarket; i++) {
            int begin = scanner.nextInt();
            int end = scanner.nextInt();
            int price = scanner.nextInt();
            supermarkets.add(new Supermarket(begin, end, price));
        }

        int sum = 0;
        for (int i = 0; i < supermarket; i++) {
            sum += choose(supermarkets, i + 1);
        }

        System.out.println(sum);
    }

    private static int choose(List<Supermarket> supermarkets, int n) {
        List<Supermarket> enabled = new ArrayList<>();
        for (Supermarket supermarket : supermarkets) {
            if (supermarket.begin <= n && supermarket.end >= n) {
                enabled.add(supermarket);
            }
        }
        if (enabled.isEmpty()) {
            return 0;
        }
        enabled.sort(Comparator.comparingInt(s -> s.price));
        return enabled.get(0).price;
    }



    private static class Supermarket {
        public int begin;
        public int end;
        public int price;

        public Supermarket(int begin, int end, int price) {
            this.begin = begin;
            this.end = end;
            this.price = price;
        }
    }


}
