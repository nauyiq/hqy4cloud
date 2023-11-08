package com.hqy.cloud.arithmetic.B;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/6/20 14:52
 */
public class NumberGame {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        List<Integer> result = new ArrayList<>();
        Map<Integer, List<Integer>> inputMap = new HashMap<>();

        int n = 0;
        int m = 0;
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            int[] ints = Arrays.stream(line.split(" ")).mapToInt(Integer::parseInt).toArray();
            if (ints.length == 0) {
                break;
            }
            if (n == 0) {
                n = ints[0];
                m = ints[1];
            } else {
                if (ints.length != n) {
                    return;
                }
                inputMap.put(m, Arrays.stream(ints).boxed().collect(Collectors.toList()));
                n = 0;
                m = 0;
            }
        }

        for (Map.Entry<Integer, List<Integer>> entry : inputMap.entrySet()) {
            Integer number = entry.getKey();
            List<Integer> list = entry.getValue();
            result.add(check(number, list));
        }

        for (Integer integer : result) {
            System.out.println(integer);
        }


    }

    private static Integer check(Integer number, List<Integer> list) {
        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            add(i, 1, result, list);
        }

        return result.stream().anyMatch(value -> value % number == 0) ? 1 : 0;
    }


    private static void add(int index, int count, List<Integer> result, List<Integer> numbers) {
        int sum = 0;
        for (int i = index; i < count; i++) {
             sum += numbers.get(i);
        }
        if (sum != 0) {
            result.add(sum);
        }

        if (count == numbers.size() - index) {
            return;
        }

        add(index, ++count, result, numbers);
    }

}
