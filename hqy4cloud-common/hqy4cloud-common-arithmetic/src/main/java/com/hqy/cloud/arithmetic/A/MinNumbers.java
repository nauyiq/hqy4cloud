package com.hqy.cloud.arithmetic.A;

import java.util.*;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/7/14 15:45
 */
public class MinNumbers {


    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String[] arrays = scanner.nextLine().trim().split(" ");
        dfs(arrays, 0, arrays.length - 1);


        List<String> values2 = new ArrayList<>();
        for (String s : result) {
            if (s.startsWith("0")) {
                values2.add(s);
            }

        }

        List<String> values = new ArrayList<>(result.size());

        if (values2.size() == result.size()) {
            for (String s : values2) {
                while (s.startsWith("0") && s.length() != 1) {
                    s = s.substring(1);
                }
                values.add(s);
            }
            values.sort((v1, v2) -> (v1 + v2).compareTo(v2 + v1));
            System.out.println(values.get(0));
        } else {
            for (String s : result) {
                if (!s.startsWith("0")) {
                    values.add(s);
                }
            }
            values.sort((v1, v2) -> (v1 + v2).compareTo(v2 + v1));
            System.out.println(values.get(0));
        }


    }
    static Set<String> result = new HashSet<>();

    private static void dfs(String[] arrays, int x, int y) {
        if (x == y) {
            String value = "";
            for (String s : arrays) {
                value = value + s;
            }
            result.add(value);
        } else {
            for (int i = x; i <= y; i++) {
                swap(arrays, x, i);
                dfs(arrays, x + 1, y);
                swap(arrays, x, i);
            }
        }


    }

    private static void swap(String[] arrays, int x, int y) {
        String temp = arrays[y];
        arrays[y] = arrays[x];
        arrays[x] = temp;
    }




}
