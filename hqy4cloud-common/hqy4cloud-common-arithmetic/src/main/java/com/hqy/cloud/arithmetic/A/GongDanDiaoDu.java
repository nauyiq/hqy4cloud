package com.hqy.cloud.arithmetic.A;

import java.util.*;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/7/13 15:26
 */
public class GongDanDiaoDu {

    static int max = 0;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        Item[] items = new Item[n];
        for (int i = 0; i < n; i++) {
            items[i] = new Item(scanner.nextInt(), scanner.nextInt());
        }

        Arrays.sort(items, (i1,i2) -> {
            if (i1.sla == i2.sla) {
                return i2.score - i1.score;
            }
            return i1.sla - i2.sla;
        });

//        for (int i = 0; i < n; i++) {
            dfs(items, 0, 0, new ArrayList<>());
//        }
        
        System.out.println(max);
    }

    private static void dfs(Item[] items, int index, int time, List<Integer> result) {
        if (index == items.length) {
            int sum = 0;
            for (Integer integer : result) {
                sum += integer;
            }

            if (sum > max) {
                max = sum;
            }
        } else {
            for (int i = index; i < items.length; i++) {
                Item item = items[i];
                result.add(time < item.sla ? item.score : 0);
                time = time < item.sla ? time + 1 : time;
                dfs(items, i + 1, time, result);
                result.remove(result.size() - 1);
            }
        }
    }



    private static class Item {
        public int sla;
        public int score;

        public Item(int sla, int score) {
            this.sla = sla;
            this.score = score;
        }
    }

}
