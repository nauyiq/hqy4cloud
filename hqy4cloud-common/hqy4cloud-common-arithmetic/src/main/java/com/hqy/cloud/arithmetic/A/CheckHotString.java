package com.hqy.cloud.arithmetic.A;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/7/4 11:25
 */
public class CheckHotString {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String[] split = scanner.nextLine().trim().split(" ");
        if (split.length != 3) {
            System.out.println(-1);
            return;
        }

        int count = Integer.parseInt(split[0]);
        int length = Integer.parseInt(split[1]);
        String steam = split[2];

        List<Character> result = new ArrayList<>();
        Map<Character, Integer> map = new TreeMap<>();
        for (int i = 0; i < steam.length(); i++) {
            char charAt = steam.charAt(i);
            map.put(charAt, map.containsKey(charAt) ? map.get(charAt) + 1 : 1);
            if ((i + 1) % length == 0) {
                find(map, count, result);
            }
        }
        String message = "";
        for (Character character : result) {
            message = message.concat(character.toString());
        }
        System.out.println(message);
    }

    private static void find(Map<Character, Integer> map, int count, List<Character> result) {
        Set<Map.Entry<Character, Integer>> entries = map.entrySet();
        List<Map.Entry<Character, Integer>> collect = entries.stream().sorted((e1, e2) -> {
            if (e2.getValue().equals(e1.getValue())) {
                return e2.getKey() - e1.getKey();
            } else {
                return e2.getValue() - e1.getValue();
            }
        }).collect(Collectors.toList());
        for (int i = 0; i < count; i++) {
            result.add(collect.get(i).getKey());
        }
    }

}
