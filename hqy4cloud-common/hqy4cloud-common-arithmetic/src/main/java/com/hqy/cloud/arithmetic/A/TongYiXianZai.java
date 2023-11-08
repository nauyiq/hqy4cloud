package com.hqy.cloud.arithmetic.A;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/7/13 14:03
 */
public class TongYiXianZai {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = Integer.parseInt(scanner.nextLine());
        Item[] items = new Item[n];
        int[] goods = Arrays.stream(scanner.nextLine().trim().split(" ")).mapToInt(Integer::parseInt).toArray();
        int[] types = Arrays.stream(scanner.nextLine().trim().split(" ")).mapToInt(Integer::parseInt).toArray();
        int carNumber = Integer.parseInt(scanner.nextLine());
        for (int i = 0; i < n; i++) {
            items[i] = new Item(goods[i], types[i]);
        }

        Map<Integer, List<Item>> map = Arrays.stream(items).collect(Collectors.groupingBy(i -> i.type));
        Collection<List<Item>> values = map.values();
        int max = 0;
        for (List<Item> itemList : values) {
            int result = findMax(itemList, carNumber);
            if (result > max) {
                max = result;
            }
        }
        System.out.println(max);

    }

    private static int findMax(List<Item> items, int car) {
        items.sort(Comparator.comparingInt(i -> i.goods));
        int sum = 0;
        if (car == 1) {
            for (Item item : items) {
                sum += item.goods;
            }
            return sum;
        } else {
            staticMax = items.get(items.size() - 1).goods;
            dfs(items, staticMax, 0, car);
            return staticMax;
        }
    }

    static int staticMax = 0;


    private static void dfs(List<Item> items, int max, int index, int count) {
        int sum = 0;
        int temp = 0;
        for (Item item : items) {
            sum += item.goods;
            if (sum >= max) {
                temp++;
                if (temp > count) {
                    staticMax = items.get(index).goods + max;
                    dfs(items, staticMax, ++index, count);
                } else {
                    sum = 0;
                }
            }
        }
    }




    private static class Item {
        public int goods;
        public int type;

        public Item(int goods, int type) {
            this.goods = goods;
            this.type = type;
        }
    }

}
