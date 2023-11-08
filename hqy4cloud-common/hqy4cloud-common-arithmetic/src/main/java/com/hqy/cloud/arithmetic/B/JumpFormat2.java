package com.hqy.cloud.arithmetic.B;

import java.util.Arrays;
import java.util.Scanner;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/6/19 14:54
 */
public class JumpFormat2 {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String[] split = scanner.nextLine().split(",");
        if (split.length == 0) {
            return;
        }
        int[] formats = Arrays.stream(split).mapToInt(Integer::parseInt).toArray();
        if (formats[0] != formats[formats.length -1]) {
            return;
        }

        if (formats.length == 2) {
            System.out.println(Math.max(formats[0], formats[1]));
        } else {
            System.out.println(Math.max(jumpRange(formats, 0, formats.length-2), jumpRange(formats, 1   , formats.length -1)));
        }



    }

    public static int jumpRange(int[] formats, int start, int end) {
        int first = formats[start];
        int second = Math.max(formats[start], formats[start + 1]);
        for (int i = start + 2; i <= end; i++) {
            int temp = second;
            int data = formats[i];
            second = Math.max(first + data, second);
            first = temp;
        }
        return second;
    }




}
