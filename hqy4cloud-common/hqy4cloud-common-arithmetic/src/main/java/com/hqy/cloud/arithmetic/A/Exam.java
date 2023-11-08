package com.hqy.cloud.arithmetic.A;

import java.util.Scanner;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/6/29 9:46
 */
public class Exam {

    static int result = 0;
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int score = scanner.nextInt();

        int[] subject = new int[25];
        for (int i = 0; i < 10; i++) {
            subject[i] = 2;
        }
        for (int i = 10; i < 20; i++) {
            subject[i] = 4;
        }
        for (int i = 20; i < 25; i++) {
            subject[i] = 8;
        }

        dfs(subject, 0, score, 0, 0);
        System.out.println(result);
    }

    private static void dfs(int[] subject, int index, int score, int count, int data) {
        if (data == score) {
            ++result;
            return;
        }

        if (data > score || count >= 3) {
            return;
        }

        for (int i = index; i < subject.length; i++) {
            data += subject[i];
            dfs(subject, i + 1, score, count, data);
            data -= subject[i];
            count++;
        }
    }


}
