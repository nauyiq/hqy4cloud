package com.hqy.cloud.arithmetic.A;

import java.util.*;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/7/14 9:23
 */
public class DiscountCoupon {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        int[] coupon = new int[3];
        coupon[0] = scanner.nextInt();
        coupon[1] = scanner.nextInt();
        coupon[2] = scanner.nextInt();

        int n = scanner.nextInt();
        int[] numbers = new int[n];
        for (int i = 0; i < n; i++) {
            numbers[i] = scanner.nextInt();
        }

        for (int number : numbers) {
            List<Item> items = new ArrayList<>();
            for (int i = 0; i < coupon.length; i++) {
                items.add(bfs(coupon, number, i));
            }
            Collections.sort(items);
            System.out.println(items.get(0).money + " " + items.get(0).count);
        }



    }


    private static Item bfs(int[] coupon, int number, int index) {
        int a = coupon[0];
        int b = coupon[1];
        int c = coupon[2];

        if (index == 0) {
            //a,b || a,c
            Item item1 = a(a, new Item(number, 0));
            item1 = b(b, item1);

            Item item2 = a(a, new Item(number, 0));
            item2 = c(c, item2);
            List<Item> items = Arrays.asList(item1, item2);
            Collections.sort(items);
            return items.get(0);
        }

        if (index == 1) {
            //b,a || b,c
            Item item1 = b(b, new Item(number, 0));
            item1 = a(a, item1);

            Item item2 = b(b, new Item(number, 0));
            item2 = c(c, item2);
            List<Item> items = Arrays.asList(item1, item2);
            Collections.sort(items);
            return items.get(0);
        }

        if (index == 2) {
            //c,a || c,b
            Item item1 = c(c, new Item(number, 0));
            item1 = a(a, item1);

            Item item2 = c(c, new Item(number, 0));
            item2 = b(b, item2);
            List<Item> items = Arrays.asList(item1, item2);
            Collections.sort(items);
            return items.get(0);
        }


        return new Item(number, 0);

    }


    private static Item a(int a, Item item) {
        int count = 0;
        if (item.money >= 100 && a > 0) {
            int i = item.money / 100;
            count = Math.min(i, a);
            item.money = item.money - count * 10;
        }
        return new Item(item.money, count + item.count);
    }

    private static Item b(int b, Item item) {
        if (b > 0) {
            String data = item.money * 0.92 + "";
            item.money = Integer.parseInt(data.substring(0, data.indexOf(".")));
            return new Item(item.money, item.count + 1);
        }
        return item;
    }

    private static Item c(int c, Item item) {
        int count = 0;
        while (item.money > 0 && c > 0) {
            int i = item.money - 5;
            item.money = Math.max(i, 0);
            count++;
            c--;
        }
        return new Item(item.money, item.count + count);
    }


    private static class Item implements Comparable<Item> {
        public int money;
        public int count;

        public Item(int money, int count) {
            this.money = money;
            this.count = count;
        }

        @Override
        public int compareTo(Item o) {
            if (this.money == o.money) {
                return this.count - o.count;
            }
            return this.money - o.money;
        }
    }


}
