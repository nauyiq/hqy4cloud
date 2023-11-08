package com.hqy.cloud.arithmetic.B;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/6/25 14:42
 */
public class AlibabaGoldBox4 {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int[] numbers = Arrays.stream(scanner.nextLine().split(",")).mapToInt(Integer::parseInt).toArray();
        if (numbers[0] != numbers[numbers.length - 1]) {
            System.out.println("无效的输入");
            return;
        }

        // 排序好的数组.
        List<Integer> sorted = Arrays.stream(numbers).boxed().collect(Collectors.toList());
        sorted.remove(sorted.size() - 1);
        sorted = sorted.stream().sorted().collect(Collectors.toList());

        int[] resultNumbers = new int[numbers.length];
        for (int i = 0; i < numbers.length; i++) {
            int number = numbers[i];
            resultNumbers[i] = getNextMaxNumber(number, sorted);
        }


        String output = "";
        for (int resultNumber : resultNumbers) {
            output = output.concat(resultNumber + ",");
        }
        output = output.substring(0, output.length() -1);
        System.out.println(output);

    }

    private static int getNextMaxNumber(int number, List<Integer> sorted) {
        if (sorted.get(sorted.size() - 1) <= number) {
            return -1;
        }

        int left = 0;
        int right = sorted.size() - 1;
        while (left <= right) {
            int mid = ( left + right ) >> 1;
            Integer midNumber = sorted.get(mid);
             if (midNumber > number) {
                right = mid - 1;
            } else {
                left = mid + 1;
            }
        }

        return sorted.get(left);
    }

}
