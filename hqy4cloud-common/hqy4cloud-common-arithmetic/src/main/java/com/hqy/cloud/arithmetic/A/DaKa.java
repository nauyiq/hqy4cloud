package com.hqy.cloud.arithmetic.A;

import java.util.*;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/7/17 11:17
 */
public class DaKa {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        int[] count = new int[30];
        for (int i = 0; i < 30; i++) {
            count[i] = scanner.nextInt();
        }
        Map<Integer, Integer> result = new HashMap<>();
        Map<Integer, Integer> time = new HashMap<>();
        for (int i = 0; i < 30; i++) {
            for (int j = 0; j < count[i]; j++) {
                int id = scanner.nextInt();
                result.put(id, result.containsKey(id) ? result.get(id) + 1 : 1);
                if (!time.containsKey(id)) {
                    time.put(id, i);
                }
            }
        }

















    }

}
