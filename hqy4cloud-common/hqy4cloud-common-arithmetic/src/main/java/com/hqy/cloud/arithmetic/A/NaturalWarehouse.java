package com.hqy.cloud.arithmetic.A;

import java.util.Arrays;
import java.util.Scanner;
import java.util.Stack;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/6/28 15:14
 */
public class NaturalWarehouse {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int itemWidth = Integer.parseInt(scanner.nextLine());
        int pitWidth = Integer.parseInt(scanner.nextLine());
        int[] pits = Arrays.stream(scanner.nextLine().trim().split(",")).mapToInt(Integer::parseInt).toArray();
        if (pits.length != pitWidth) {
            return;
        }


        System.out.println(dfs(pits, itemWidth));

    }

    private static int dfs(int[] pits, int itemWidth) {
        int[] newPits = new int[pits.length + 2];
        System.arraycopy(pits, 0, newPits, 1, pits.length);
        Stack<Integer> stack = new Stack<>();
        int result = 0;

        for (int i = 0; i < newPits.length; i++) {
            while (!stack.isEmpty() && newPits[stack.peek()] < 0 && newPits[i] > newPits[stack.peek()]) {
                Integer index = stack.pop();
                int deep = newPits[index];

                if (stack.empty()) {
                    break;
                }

                int leftIndex = stack.peek();
                int leftDeep = newPits[leftIndex];

                result += (i - leftIndex - 1) / itemWidth * (Math.min(leftDeep, newPits[i]) - deep);
            }
            stack.push(i);
        }

        return result;

    }


}
