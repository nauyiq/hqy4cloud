package com.hqy.cloud.arithmetic.B;

import java.util.*;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/6/20 17:49
 */
public class AlibabaGoldBox5 {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int[] boxes = Arrays.stream(scanner.nextLine().split(",")).mapToInt(Integer::parseInt).toArray();

        int length = boxes.length;
        if (length < 1 || length > 100000) {
            return;
        }

        int k = scanner.nextInt();
        if (k >= length) {
            return;
        }

        List<Integer> result = new ArrayList<>();
        findMax(result, 0, k, boxes);
        System.out.println(Collections.max(result));

    }

    public static void findMax(List<Integer> result, int index, int k, int[] boxes) {
        int sum = 0;
        for (int i = index; i < k + index; i++) {
            sum += boxes[i];
        }
        result.add(sum);

        if (index + k == boxes.length) {
            return;
        }

        findMax(result, index + 1, k, boxes);
    }


}
