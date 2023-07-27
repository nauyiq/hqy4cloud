package com.hqy.cloud.arithmetic.A;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/7/6 16:55
 */
public class MaxRightTriangle {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = Integer.parseInt(scanner.nextLine().trim());

        List<List<Integer>> arrays = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            List<Integer> numbers = Arrays.stream(scanner.nextLine().trim().split(" ")).mapToInt(Integer::parseInt).boxed().collect(Collectors.toList());
            arrays.add(numbers);
        }
        for (List<Integer> array : arrays) {
            System.out.println(dfs(array));
        }
    }

    private static int dfs(List<Integer> array) {
        int result = 0;
        Set<Integer> queue = new LinkedHashSet<>();
        for (int i = 0; i < array.size(); i++) {
            int a = array.get(i);
            queue.add(i);
            for (int j = i + 1; j < array.size() && !queue.contains(j); j++) {
               int b = array.get(j);
               queue.add(j);
                for (int k = j + 1; k < array.size() && !queue.contains(k); k++) {
                    int c = array.get(k);
                    List<Integer> triangle = Arrays.asList(a, b, c);
                    triangle.sort(Comparator.comparingInt(t -> t));
                    boolean flag = triangle.get(0) * triangle.get(0) + triangle.get(1) * triangle.get(1) == triangle.get(2) * triangle.get(2);
                    if (flag) {
                        result++;
                        queue.add(k);
                    } else {
                        queue.remove(i);
                        queue.remove(j);
                    }
                }
            }
        }

        return result;
    }

}
