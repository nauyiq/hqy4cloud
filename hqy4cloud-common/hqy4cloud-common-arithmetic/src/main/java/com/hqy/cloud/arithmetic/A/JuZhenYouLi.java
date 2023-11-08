package com.hqy.cloud.arithmetic.A;

import java.util.*;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/6/30 13:44
 */
public class JuZhenYouLi {

    static List<Integer> result = new ArrayList<>();
    static int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int m = scanner.nextInt();
        int n = scanner.nextInt();
        int[][] matrix = new int[m][n];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                matrix[i][j] = scanner.nextInt();
            }
        }

        dfs(matrix, m, n, 0, new Step(0, 0), new HashSet<>(), 0);
        result.sort(Comparator.comparingInt(a -> a));
        System.out.println(result.get(0));
    }



    private static void dfs(int[][] matrix, int row, int column, int count, Step step, Set<Step> preSteps, int sum) {
        if (step.x == row - 1 && step.y == column - 1){
            result.add(sum);
            return;
        }

        if (count > 1) {
            return;
        }

        List<Step> positions = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            int new_x = step.x + directions[i][0];
            int new_y = step.y + directions[i][1];
            Step newStep = new Step(new_x, new_y);
            if (new_x >= 0 && new_x < row && new_y >= 0 && new_y < column && !preSteps.contains(newStep)) {
                positions.add(newStep);
            }
        }

        List<Step> sameValues = new ArrayList<>();
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < column; j++) {
                Step newStep = new Step(i, j);
                if (i != step.x && j != step.y && matrix[i][j] == matrix[step.x][step.y] && !preSteps.contains(newStep)) {
                    sameValues.add(newStep);
                }
            }
        }

        Map<Step, Boolean> map = new HashMap<>();
        if (sameValues.isEmpty()) {
            for (Step position : positions) {
                map.put(position, false);
            }
        } else {
            for (Step position : positions) {
                map.put(position, false);
            }
            for (Step sameValue : sameValues) {
                map.put(sameValue, true);
            }
        }

        if (!map.isEmpty()) {
            for (Map.Entry<Step, Boolean> entry : map.entrySet()) {
                Step position = entry.getKey();
                Boolean useCount = entry.getValue();
                int newSum = (Math.abs(matrix[position.x][position.y] - matrix[step.x][step.y])) + sum;
                Set<Step> newSteps = new HashSet<>(preSteps);
                newSteps.add(position);
                dfs(matrix, row, column, useCount ? 1 : 0, position, newSteps, newSum);
            }
        }

    }


    private static class Step {
        public int x;
        public int y;

        public Step(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Step step = (Step) o;
            return x == step.x && y == step.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }
    }

}
