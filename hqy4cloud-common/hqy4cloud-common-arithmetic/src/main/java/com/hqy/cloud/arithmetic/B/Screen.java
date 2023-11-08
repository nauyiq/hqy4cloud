package com.hqy.cloud.arithmetic.B;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/6/20 10:45
 */
public class Screen {

    static int x_max = 800;
    static int y_max = 600;
    static int t_max = 100000;
    static int x;
    static int y;
    static List<List<Integer>> point = Arrays.asList(Arrays.asList(0, 0), Arrays.asList(750, 0), Arrays.asList(0, 575), Arrays.asList(750, 575));


    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String nextLine = scanner.nextLine();
        if (nextLine.length() == 0) {
            return;
        }

        String[] data = nextLine.split(" ");
        if (data.length != 3) {
            return;
        }

        x = Integer.parseInt(data[0]);
        y = Integer.parseInt(data[1]);
        int t = Integer.parseInt(data[2]);

        int x_step = 1;
        int y_step = 1;
        for (int i = 0; i < t; i++) {
            if (x == 0) {
                x_step = 1;
            }

            if (y == 0) {
                y_step = 1;
            }

            if (x + 50 == x_max) {
                x_step = -1;
            }

            if (y + 25 == y_max) {
                y_step = -1;
            }

            x += x_step;
            y += y_step;
        }
        System.out.println(x + " " + y);
    }




}
