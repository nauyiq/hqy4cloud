package com.hqy.cloud.arithmetic.A;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/7/13 16:58
 */
public class XinPianZiYuanZhanYong {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        //芯片资源数目
        int m = Integer.parseInt(scanner.nextLine());
        //芯片数目
        int n = Integer.parseInt(scanner.nextLine());
        //板卡
        int[][] matrix = new int[n][m];
        Map<Integer, Integer> map = new ConcurrentHashMap<>();
        for (int i = 0; i < n; i++) {
            map.put(i, 0);
        }

        //配置规则
        String rule = scanner.nextLine();

        for (int i = 0; i < rule.length(); i++) {
            char charAt = rule.charAt(i);
            List<Map.Entry<Integer, Integer>> entries = map.entrySet().stream()
                    .filter(e -> e.getValue() < m)
                    .sorted((e1, e2) -> e2.getValue() - e1.getValue()).collect(Collectors.toList());
            if (charAt == 'A') {
                // ==m已经过滤 因此占用1个肯定是足够的
                Map.Entry<Integer, Integer> entry = entries.get(0);
                Integer key = entry.getKey();
                Integer value = entry.getValue();
                matrix[key][value] = 1;
                map.put(key, value + 1);
            }
            if (charAt == 'B') {
                for (Map.Entry<Integer, Integer> entry : entries) {
                    Integer value = entry.getValue();
                    if (m - value  > 2) {
                        Integer key = entry.getKey();
                        matrix[key][value] = 1;
                        matrix[key][value + 1] = 1;
                        map.put(key, value + 2);
                    }
                }
            }

            if (charAt == 'C') {
                for (Map.Entry<Integer, Integer> entry : entries) {
                    Integer value = entry.getValue();
                    if (m - value >= 8) {
                        Integer key = entry.getKey();
                        for (int j = 0; j < 8; j++) {
                            matrix[key][value + j] = 1;
                        }
                        map.put(key, value + 8);
                    }
                }
            }
        }

        List<Map.Entry<Integer, Integer>> entries = map.entrySet().stream().sorted(Comparator.comparingInt(Map.Entry::getValue)).collect(Collectors.toList());
        for (Map.Entry<Integer, Integer> entry : entries) {
            String output = "";
            for (int i : matrix[entry.getKey()]) {
                output = output + i;
            }
            System.out.println(output);
        }

    }





}
