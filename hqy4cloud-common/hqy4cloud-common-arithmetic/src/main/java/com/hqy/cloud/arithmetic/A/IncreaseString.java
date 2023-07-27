package com.hqy.cloud.arithmetic.A;

import java.util.Scanner;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/7/14 17:01
 */
public class IncreaseString {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();

        int dpA = 0;
        int dpB = 0;

        for (int i = 0; i < input.length(); i++) {
            char charAt = input.charAt(i);
            int new_dpA = dpA;
            int new_dpB = Math.min(dpA, dpB);

            if (charAt == 'B') {
                new_dpA++;
            } else {
                new_dpB++;
            }
            dpA = new_dpA;
            dpB = new_dpB;
        }

        System.out.println(Math.min(dpA, dpB));

    }

}
