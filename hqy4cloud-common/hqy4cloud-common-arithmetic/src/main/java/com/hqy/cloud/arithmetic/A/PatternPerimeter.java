package com.hqy.cloud.arithmetic.A;

import java.util.*;

/**
 * 计算相同数字组成图形的周长
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/6/30 16:52
 */
public class PatternPerimeter {

    static int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = Integer.parseInt(scanner.nextLine().trim());


        List<List<Position>> positions = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            int[] array = Arrays.stream(scanner.nextLine().trim().split(" ")).mapToInt(Integer::parseInt).toArray();
            List<Position> positionList = new ArrayList<>();
            for (int j = 1; j < array.length; j = j + 2) {
                int x = array[j];
                int y = array[j + 1];
                Position position = new Position(x, y);
                positionList.add(position);
            }
            positions.add(positionList);
        }

        for (List<Position> position : positions) {
            System.out.println(dfs(position, positions));
        }
    }

    public static int dfs(List<Position> positions, List<List<Position>> allPositions) {
        int result = 0;
        // 又左上角图标列表positions 转成构成图形的position.
        List<Graph> graphPositions = translateGraph(positions);
        for (Graph graph : graphPositions) {
            int perimeter = 4;
            for (int i = 0; i < 4; i++) {
                double new_x = graph.x + directions[i][0];
                double new_y = graph.y + directions[i][1];
                if (new_x >= 0 && new_x < 64 && new_y >= 0 && new_y < 64) {
                    if (checkExist(graphPositions, new Graph(new_x, new_y))) {
                        perimeter--;
                    }
                }

            }
            result += perimeter;
        }
        return result;
    }

    private static List<Graph> translateGraph(List<Position> positions) {
        List<Graph> graph = new ArrayList<>();

        for (Position position : positions) {
            graph.add(new Graph(position.x + 0.5, position.y + 0.5));
        }
        return graph;
    }

    public static boolean checkExist(List<Graph> graphs, Graph check) {
        for (Graph graph : graphs) {
            if (graph.x == check.x && graph.y == check.y) {
                return true;
            }
        }
        return false;
    }

    public static class Graph {
        public double x;
        public double y;
        public Graph(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }


    public static class Position {
        public int x;
        public int y;

        public Position(int x, int y) {
            this.x = x;
            this.y = y;
        }

    }


}
