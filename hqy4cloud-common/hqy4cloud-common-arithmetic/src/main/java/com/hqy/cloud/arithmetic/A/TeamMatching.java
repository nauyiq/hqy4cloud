package com.hqy.cloud.arithmetic.A;

import java.util.*;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/7/7 11:27
 */
public class TeamMatching {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        int d = scanner.nextInt();

        if (n < 2 || n > 50) {
            System.out.println(-1);
            return;
        }

        int[] teams = new int[n];
        for (int i = 0; i < n; i++) {
            teams[i] = scanner.nextInt();
        }

        dfs(teams, d);
    }

    private static void dfs(int[] teams, int d) {
        Arrays.sort(teams);
        List<Integer> newTeams = new ArrayList<>();
        Set<Integer> indexes = new HashSet<>();
        for (int i = 0; i < teams.length - 1; i ++) {
            int current = teams[i];
            if (teams[i + 1] - current <= d && !indexes.contains(i)) {
                indexes.add(i);
                indexes.add(i + 1);
                newTeams.add(current);
                newTeams.add(teams[i + 1]);
            }

        }

        int sum = 0;
        if (newTeams.size() % 2 == 0) {
            //一定是两两匹配 然后可搭配的组合最长
            for (int i = 0; i < newTeams.size(); i = i + 2) {
                sum += newTeams.get(i + 1) - newTeams.get(i);
            }
            System.out.println(sum);
        } else {
            int temp = -1;
            for (int i = 0; i < newTeams.size() - 1; i++) {
                if (newTeams.get(i + 1) - newTeams.get(i) > temp) {
                    temp = i;
                }
            }
            newTeams.remove(newTeams.get(temp));
            //一定是两两匹配 然后可搭配的组合最长
            for (int i = 0; i < newTeams.size(); i = i + 2) {
                sum += newTeams.get(i + 1) - newTeams.get(i);
            }
            System.out.println(sum);
        }




    }



}
