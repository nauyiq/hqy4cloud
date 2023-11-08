package com.hqy.cloud.arithmetic.B;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 文件目录大小
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/6/15 17:13
 */
public class FileDirectorySize {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int m = sc.nextInt();
        int n = sc.nextInt();

        if (m > 100 || m < 1 || n > 100 || n < 1) {
            System.out.println("Error input.");
            return;
        }

        //文件夹目录
        Map<Integer, List<Integer>> dirMap = new HashMap<>(m);
        Map<Integer, Integer> dirIdMap = new HashMap<>(m + 1);

        for (int i = 0; i < m; i++) {
            int id = sc.nextInt();
            int size = sc.nextInt();
            dirIdMap.put(id, size);

            String next = sc.next();
            if (next.length() > 2 && next.contains("(") && next.contains(")")) {
                List<Integer> childDir = Arrays.stream(next.substring(1, next.length() - 1).split(",")).map(Integer::parseInt).collect(Collectors.toList());
                dirMap.put(id, childDir);
            }
        }
        System.out.println(dfs(dirIdMap, dirMap, n));
    }

    public static int dfs(Map<Integer, Integer> dirIdMap, Map<Integer, List<Integer>> dirMap, int target) {
        List<Integer> data = dirMap.get(target);
        if (data.isEmpty()) {
            return dirIdMap.get(target);
        }

        int sum = dirIdMap.get(target);

        for (Integer datum : data) {
            sum += dirIdMap.get(datum);
            List<Integer> children = dirMap.getOrDefault(datum, new ArrayList<>());
            while (!children.isEmpty()) {
                for (Integer child : children) {
                    sum += dirIdMap.getOrDefault(child, 0);
                    children = dirMap.getOrDefault(child, new ArrayList<>());
                }
            }
        }

        return sum;

    }



}
