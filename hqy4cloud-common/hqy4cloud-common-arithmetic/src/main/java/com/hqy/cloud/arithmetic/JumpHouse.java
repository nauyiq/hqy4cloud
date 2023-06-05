package com.hqy.cloud.arithmetic;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 假设房子的总格数是count，小红每回合可能连续跳的步数都放在数据steps中，
 * 游戏参与者需要分多个回合按顺序跳到第1格直到房子的最后一格，然后获得一次选房子的机会，直到所有房子都被选完，房子最多的人获胜。
 * 请问数组中是否有一种步数的组合，可以让小红三个回合跳到最后一格？如果有，请输出索引和最小的步数组合，数据保证索引和最小的步数组合是唯一的。
 * @author qiyuan.hong
 * @date 2023-06-05 21:00
 */
public class JumpHouse {

    public static int min = 10000;
    public static int steps = 3;
    public static int count;
    public static List<Integer> minIndexList;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String inputCount = scanner.nextLine();
        if (inputCount == null || inputCount.length() == 0) {
            throw new UnsupportedOperationException();
        }

        count = Integer.parseInt(inputCount);
        List<Integer> inputSteps = Arrays.stream(scanner.next().split(",")).map(Integer::parseInt).collect(Collectors.toList());
        minIndexList = new ArrayList<>();

        //选出索引和最小的步数组合
        selectMinSteps(inputSteps, steps, minIndexList, new ArrayList<>(), 0);
        System.out.println(minIndexList);
    }

    private static void selectMinSteps(List<Integer> inputSteps, int step, List<Integer> currentSteps, List<Integer> currentIndexList, int index) {
        // 三个回合跳到最后一个
        if (step == 0) {
            int total = 0;
            int indexTotal = 0;
            for (int i = 0; i < steps; i++) {
                total += currentSteps.get(i);
                indexTotal += currentIndexList.get(i);
            }

            if (total == count && total < min) {
                System.out.println(currentSteps);
                System.out.println(currentIndexList);
                min = indexTotal;
                minIndexList = new ArrayList<>(currentIndexList);
            }

        } else {
            for (int i = index; i < inputSteps.size(); i++) {
                currentSteps.add(inputSteps.get(i));
                currentIndexList.add(i);
                selectMinSteps(inputSteps, steps - 1, currentSteps, currentIndexList, i +1);
                currentSteps.remove(currentSteps.size() - 1);
                currentIndexList.remove(currentIndexList.size() - 1);
            }
        }



    }


}
