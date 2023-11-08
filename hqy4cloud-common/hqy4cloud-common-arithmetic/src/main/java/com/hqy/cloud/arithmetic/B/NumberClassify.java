package com.hqy.cloud.arithmetic.B;

import java.util.*;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/6/21 10:35
 */
public class NumberClassify {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String[] split = scanner.nextLine().split(" ");
        if (split.length != 12) {
            return;
        }

        int[] numbers = Arrays.stream(split).mapToInt(Integer::parseInt).toArray();
        int c = numbers[0];
        int b = numbers[1];
        Map<Integer, Integer> availableNumber = new HashMap<>();

        for (int i = 2; i < numbers.length; i++) {
            int value = getSum(numbers[i]) % b;
            if (value < c) {
                availableNumber.put(value, availableNumber.containsKey(value) ? availableNumber.get(value) + 1 : 1);
            }
        }

        int max = 0;
        for (Map.Entry<Integer, Integer> entry : availableNumber.entrySet()) {
            Integer key = entry.getKey();
            Integer value = entry.getValue();
            if (value > max) {
                max = value;
            }
        }
        System.out.println(max);

    }

    private static int getSum(int a) {
        int sum = 0;
        for (int i = 0; i < 4; i++) {
            sum += (byte) (a >> (i * 8));
        }
        return sum;
    }

}
