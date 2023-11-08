package com.hqy.cloud.arithmetic.A;

import java.util.*;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/7/3 17:23
 */
public class ChengFaMa {

    static Set<Integer> result = new HashSet<>();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();

        int[] weight = new int[n];
        int[] count = new int[n];

        for (int j = 0; j < n; j++) {
            weight[j] = scanner.nextInt();
        }
        for (int j = 0; j < n; j++) {
            count[j] = scanner.nextInt();
        }

        bfs(weight, count);
        System.out.println(result.size());
    }

    private static void bfs(int[] weightMatrix, int[] countMatrix) {
        List<Integer> matrix = new ArrayList<>();
        for (int i = 0; i < weightMatrix.length; i++) {
            int weight = weightMatrix[i];
            int count = countMatrix[i];
            for (int j = 0; j < count; j++) {
                matrix.add(weight);
            }
        }

        result.add(0);
        sum(matrix, 0);
    }


    private static void sum(List<Integer> matrix, int index) {
        if (index == matrix.size()) {
            return;
        }

        int sum = 0;
        for (int i = index; i < matrix.size(); i++) {
            sum += matrix.get(i);
            result.add(sum);
        }

        sum(matrix, ++index);
    }



}
