package com.hqy.cloud.arithmetic.B;

import java.util.*;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/6/21 13:35
 */
public class FileCopy {

    static int capacity = 1474560;
    static int block = 512;
    static Map<Integer, Integer> result = new HashMap<>();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        List<Integer> inputFiles = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            inputFiles.add(scanner.nextInt());
        }

        if (inputFiles.isEmpty()) {
            return;
        }
        List<Integer> realFiles = new ArrayList<>(inputFiles.size());
        for (Integer file : inputFiles) {
            int readFile = file % block == 0 ? file : file + block - (file % block);
            realFiles.add(readFile);
        }

        dfs(realFiles, inputFiles);

        Set<Integer> set = result.keySet();
        Integer integer = Collections.max(set);
        System.out.println(result.get(integer));
    }

    public static void dfs(List<Integer> files, List<Integer> inputFiles) {
        for (int i = 0; i < files.size(); i++) {
            add(files, inputFiles, 0);
        }
    }

    public static void add(List<Integer> files, List<Integer> inputFiles, int index) {
        if (index == files.size()) {
            return;
        }
        int sum = 0;
        int inputSum = 0;
        for (int i = 0; i < index + 1 ; i++) {
            sum += files.get(i);
            if (sum <= capacity) {
                result.put(sum, inputSum += inputFiles.get(i));
            }

        }
        add(files, inputFiles, ++index);
    }



}
