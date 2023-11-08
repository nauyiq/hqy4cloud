package com.hqy.cloud.arithmetic.A;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/7/17 10:45
 */
public class YuanQuZhanDian {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int number = scanner.nextInt();
        int[][] matrix = new int[number][2];
        int max = 0;
        int min = Integer.MAX_VALUE;
        for (int i = 0; i < number; i++) {
            for (int j = 0; j < 2; j++) {
                int value = scanner.nextInt();
                max = Math.max(max, value);
                min = Math.min(min, value);
                matrix[i][j] = value;
            }
        }

        Map<Integer, Integer> map = new HashMap<>(number);
        for (int[] ints : matrix) {
            int begin = ints[0];
            int end = ints[1];

            if (begin == end) {
                map.put(begin, map.containsKey(begin) ? map.get(begin) + 1 : 1);
            } else if (begin < end) {
                for (int j = begin; j <= end; j++) {
                    map.put(j, map.containsKey(j) ? map.get(j) + 1 : 1);
                }
            } else {
                for (int j = begin; j <= max; j++) {
                    map.put(j, map.containsKey(j) ? map.get(j) + 1 : 1);
                }
                for (int j = min; j < end; j++) {
                    map.put(j, map.containsKey(j) ? map.get(j) + 1 : 1);
                }
            }

        }

        List<Map.Entry<Integer, Integer>> entries = map.entrySet().stream().sorted((e1, e2) -> {
            if (e1.getValue().equals(e2.getValue())) {
                return e1.getKey() - e2.getKey();
            }
            return e2.getValue() - e1.getValue();
        }).collect(Collectors.toList());

        Map.Entry<Integer, Integer> entry = entries.get(0);
        System.out.println(entry.getKey());
    }




}
