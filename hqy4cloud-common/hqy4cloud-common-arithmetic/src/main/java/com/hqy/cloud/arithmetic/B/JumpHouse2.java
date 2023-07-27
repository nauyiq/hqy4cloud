package com.hqy.cloud.arithmetic.B;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 跳房子2
 * @author qiyuan.hong
 * @date 2023-06-05 21:00
 */
public class JumpHouse2 {

    static int indexSum = Integer.MAX_VALUE;
    static int count;
    static List<Integer> result;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String inputCount = scanner.nextLine();
        String inputSteps= scanner.nextLine();
        if (inputCount == null || inputCount.length() == 0 || inputSteps == null || inputSteps.length() == 0) {
            System.out.println("Error input.");
            return;
        }
        String[] inputStringSteps = inputSteps.split(",");
        if (inputStringSteps.length == 0) {
            System.out.println("Error input.");
            return;
        }

        // 输入的总数.
        count = Integer.parseInt(inputCount);
        // 输入的步骤
        int[] steps = Arrays.stream(inputStringSteps).mapToInt(Integer::parseInt).toArray();
        dfs(steps, 3, new ArrayList<>(), new ArrayList<>(), 0);

        String output = "[";
        for (Integer integer : result) {
            output = output + integer + ",";
        }
        output = output.substring(0, output.lastIndexOf(",")) + "]";
        System.out.println(output);
    }


    public static void dfs(int[] steps, int num, List<Integer> data, List<Integer> indexList, int index) {
        if (num == 0) {
            int sum = 0;
            int concurrentIndexSum = 0;
            for (int i = 0; i < 3; i++) {
                sum += data.get(i);
                concurrentIndexSum += indexList.get(i);
            }

            if (sum == count && concurrentIndexSum < indexSum) {
                indexSum = concurrentIndexSum;
                result = new ArrayList<>(data);
            }
        } else {
            for (int i = index; i < steps.length; i++) {
                data.add(steps[i]);
                indexList.add(i);
                dfs(steps, num - 1, data, indexList, i + 1);
                indexList.remove(indexList.size() - 1);
                data.remove(data.size() - 1);
            }

        }




    }



}
