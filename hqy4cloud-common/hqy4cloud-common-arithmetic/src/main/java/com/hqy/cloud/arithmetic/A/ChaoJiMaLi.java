package com.hqy.cloud.arithmetic.A;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/7/3 11:09
 */
public class ChaoJiMaLi {

    static int result = 0;
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        //生命数
        int life = scanner.nextInt();
        //吊桥的长度
        int length = scanner.nextInt();
        //缺失的木板数
        int k = scanner.nextInt();
        List<Integer> broads = new ArrayList<>(k);
        for (int i = 0; i < k; i++) {
            broads.add(scanner.nextInt());
        }

        dfs(length, life, 0, broads);

        System.out.println(result);

    }

    private static void dfs(int length, int life, int steps, List<Integer> broads) {
        if (life <= 0 || steps > length + 1) {
            return;
        }

        if (steps == length + 1) {
            result++;
            return;
        }

        int firstStep = steps + 1;
        int newLife = broads.contains(firstStep) ? life - 1 : life;
        dfs(length, newLife, firstStep, broads);

        int secondStep = steps + 2;
        int newLife2 = broads.contains(secondStep) ? life - 1 : life;
        dfs(length, newLife2, secondStep, broads);

        int threeStep = steps + 3;
        int newLife3 = broads.contains(threeStep) ? life - 1 : life;
        dfs(length, newLife3, threeStep, broads);
    }




}
