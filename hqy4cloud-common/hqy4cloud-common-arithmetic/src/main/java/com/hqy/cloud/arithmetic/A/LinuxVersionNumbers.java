package com.hqy.cloud.arithmetic.A;

import java.util.*;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/7/14 11:23
 */
public class LinuxVersionNumbers {

    static Map<Integer, Integer> result = new HashMap<>();
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        int[][] matrix = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                matrix[i][j] = scanner.nextInt();
            }
        }

        Map<Integer, Set<Integer>> map = new HashMap<>();
        for (int i = 0; i < n; i++) {
            Set<Integer> set = map.computeIfAbsent(i, v -> new HashSet<>());
            for (int j = 0; j < n; j++) {
                if (matrix[i][j] == 1) {
                    set.add(j);
                }
            }
        }

        dfs(matrix, 0, new HashSet<>(), map);

        List<Integer> values = new ArrayList<>(result.values());
        Collections.sort(values);
        System.out.println(values.get(values.size() - 1));

    }

    private static void dfs(int[][] matrix, int index, Set<Integer> lastIndex, Map<Integer, Set<Integer>> map) {
        Set<Integer> set = map.get(index);
        if (!set.isEmpty()) {
            for (Integer version : set) {
                result.put(index, result.containsKey(index) ? result.get(index) + 1 : 1);
                if (version != index && !lastIndex.contains(version)) {
                    Set<Integer> indexes = new HashSet<>(lastIndex);
                    indexes.add(index);
                    dfs(matrix, version, indexes, map);
                }
            }
        }

    }



}
