package com.hqy.cloud.arithmetic.A;

import java.util.*;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/6/29 15:53
 */
public class XiTianQuJing {

    static List<Integer> result = new ArrayList<>();
    static int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int rows = scanner.nextInt();
        int column = scanner.nextInt();
        int maxHeight = scanner.nextInt();

        int[][] matrix = new int[rows][column];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < column; j++) {
                matrix[i][j] = scanner.nextInt();
            }
        }

        Step step = new Step(0, 0, 0);
        HashSet<Step> set = new HashSet<>();
        set.add(step);
        dfs(matrix, maxHeight, rows, column, step, set, 0);

        if (result.isEmpty()) {
            System.out.println(-1);
        } else {
            result.sort(Comparator.comparingInt(s -> s));
            System.out.println(result.get(0));
        }
    }

    private static void dfs(int[][] matrix, int maxHeight, int rows, int column, Step step, Set<Step> pre_xy, int steps) {
        if (step.x == rows - 1 && step.y == column - 1) {
            result.add(steps);
            return;
        }

        if (step.count >= 3) {
            return;
        }

        // 获取当前点的可以移动的坐标点.
        List<Step> positions = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            int new_x = step.x + directions[i][0];
            int new_y = step.y + directions[i][1];

            if (new_x >= 0 && new_x < rows && new_y >= 0 && new_y < column) {
                Step newStep = new Step(new_x, new_y);
                if (!pre_xy.contains(newStep)) {
                    positions.add(newStep);
                }
            }
        }

        for (Step position : positions) {
            int nexHeight = matrix[position.x][position.y];
            int height = matrix[step.x][step.y];

            int count = nexHeight - height > maxHeight ? step.count + 1 : step.count;
            Step newStep = new Step(position.x, position.y, count);
            Set<Step> copySet = new HashSet<>(pre_xy);
            copySet.add(newStep);

            int new_step = steps + 1;
            dfs(matrix, maxHeight, rows, column, newStep, copySet, new_step);
        }



    }



    private static class Step {
        public int x;
        public int y;
        public int count;

        public Step(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public Step(int x, int y, int count) {
            this.x = x;
            this.y = y;
            this.count = count;
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
