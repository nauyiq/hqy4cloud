package com.hqy.cloud.arithmetic.A;

import java.util.Arrays;
import java.util.Scanner;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/7/6 16:24
 */
public class JiHePingJunShu {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int[] ints = Arrays.stream(scanner.nextLine().trim().split(" ")).mapToInt(Integer::parseInt).toArray();
        int n = ints[0];
        int l = ints[1];

        double[] doubles = new double[n];
        for (int i = 0; i < n; i++) {
            doubles[i] = scanner.nextDouble();
        }











    }

}
