package com.hqy.cloud.arithmetic.A;

import java.util.Arrays;
import java.util.Scanner;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/7/13 17:33
 */
public class LogLimit {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        int[] numbers = new int[n];
        int sum = 0;
        for (int i = 0; i < n; i++) {
            int nextInt = scanner.nextInt();
            numbers[i] = nextInt;
            sum += nextInt;
        }
        int total = scanner.nextInt();

        if (sum <= total) {
            System.out.println(-1);
            return;
        }

        Arrays.sort(numbers);

        int left = numbers[0];
        int right = numbers[n - 1];

        while (left < right) {
            int mid = (left + right) >> 1;
            sum = 0;
            for (int number : numbers) {
                sum += Math.min(number, mid);
            }

            if (sum == total) {
                left = mid;
                break;
            }

            if (sum > total) {
                right = mid - 1;
            } else {
                left = mid;
            }
        }

        System.out.println(left);





    }
    
}
