package com.hqy.cloud.arithmetic.A;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/7/4 17:15
 */
public class HuiWenString {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String line = scanner.nextLine();
        if (line.length() <= 2) {
            System.out.println(line);
            return;
        }

        Map<Character, Integer> map = new HashMap<>();
        for (int i = 0; i < line.length(); i++) {
            char charAt = line.charAt(i);
            map.put(charAt, map.containsKey(charAt) ? map.get(charAt) + 1 : 1);
        }

        Map<Character, Integer> even = new HashMap<>();
        Set<Character> odd = new HashSet<>();

        for (Map.Entry<Character, Integer> entry : map.entrySet()) {
            Integer value = entry.getValue();
            if (value % 2 == 0 ){
                even.put(entry.getKey(), value);
            } else {    
                if (value > 2) {
                    even.put(entry.getKey(), value - 1);
                }
                odd.add(entry.getKey());
            }
        }

        String output = "";

        Set<Map.Entry<Character, Integer>> entries = even.entrySet();
        List<Map.Entry<Character, Integer>> collect = entries.stream().sorted((e1, e2) -> e1.getKey() - e2.getKey()).collect(Collectors.toList());
        for (Map.Entry<Character, Integer> entry : collect) {
            Character key = entry.getKey();
            Integer count = entry.getValue();
            for (int i = 0; i < count / 2; i++) {
                output = output.concat(key.toString());
            }
        }
        if (!odd.isEmpty()) {
            Character character = odd.stream().sorted(Comparator.comparingInt(o -> o)).collect(Collectors.toList()).get(0);
            output = output.concat(character.toString());
        }

        for (int i = output.length() - 2; i >= 0 ; i--) {
            output = output.concat(output.charAt(i) + "");
        }
        System.out.println(output);

    }


}
