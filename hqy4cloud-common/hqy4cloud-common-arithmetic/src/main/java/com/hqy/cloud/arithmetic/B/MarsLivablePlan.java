package com.hqy.cloud.arithmetic.B;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/6/28 11:19
 */
public class MarsLivablePlan {
    static final int DEATH = -1;
    static final int LIVABLE = 1;
    static final int UNLIVABLE = 0;
    static int date = 1;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        List<List<Integer>> matrixList = new ArrayList<>();

        while (scanner.hasNextLine()) {
            try {
                String nextLine = scanner.nextLine();
                if (nextLine.length() != 0) {
                    String[] grid = nextLine.split(" ");
                    if (grid.length == 0) {
                        break;
                    }
                    List<Integer> list = new ArrayList<>(grid.length);
                    for (String data : grid) {
                        switch (data) {
                            case "YES":
                                list.add(LIVABLE);
                                break;
                            case "NO":
                                list.add(UNLIVABLE);
                                break;
                            case "NA":
                                list.add(DEATH);
                                break;
                        }
                    }
                    matrixList.add(list);
                }
            } catch (Exception e) {
                break;
            }
        }

        if (matrixList.isEmpty()) {
            return;
        }

        //转数组.
        int row = matrixList.size();
        int column = matrixList.get(0).size();

        int[][] matrix = new int[row][column];
        for (int i = 0; i < matrixList.size(); i++) {
            for (int j = 0; j < matrixList.get(i).size(); j++) {
                matrix[i][j] = matrixList.get(i).get(j);
            }
        }

        dfs(matrix, row, column, 0);
        System.out.println(date);

    }

    private static void dfs(int[][] matrix, int row, int column, int result) {
        date = result;
        boolean end = true;
        int[][] clone = clone(matrix, row, column);
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < column; j++) {
                int status = matrix[i][j];
                if (status == LIVABLE) {
                    //遍历四个方位.
                    if (i - 1 >= 0) {
                        update(clone, i - 1, j);
                    }
                    if (i + 1 < row) {
                        update(clone, i + 1, j);
                    }
                    if (j - 1 >= 0) {
                        update(clone, i, j - 1);
                    }
                    if (j + 1 < column) {
                        update(clone, i, j + 1);
                    }

                }

                if (status == UNLIVABLE) {
                    end = false;
                    boolean interrupt = false;
                    if (i - 1 >= 0) {
                        interrupt = interrupt(clone, i - 1, j);
                        if (!interrupt) {
                            continue;
                        }
                    }
                    if (i + 1 < row) {
                        interrupt = interrupt(clone, i + 1, j);
                        if (!interrupt) {
                            continue;
                        }
                    }
                    if (j - 1 >= 0) {
                        interrupt = interrupt(clone, i, j - 1);
                        if (!interrupt) {
                            continue;
                        }
                    }
                    if (j + 1 < column) {
                        interrupt = interrupt(clone, i, j + 1);
                        if (!interrupt) {
                            continue;
                        }
                    }

                    if (interrupt) {
                        date = -1;
                        return;
                    }
                }
            }
        }

        if (end) {
            return;
        }
        dfs(clone, row, column, ++result);


    }

    private static int[][] clone(int[][] matrix, int m, int n) {
        int[][] clone = new int[m][n];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                clone[i][j] = matrix[i][j];
            }
        }
        return clone;
    }



    private static void update(int[][] matrix, int x, int y) {
        int i = matrix[x][y];
        if (i == UNLIVABLE) {
            matrix[x][y] = LIVABLE;
        }
    }

    private static boolean interrupt(int[][] matrix, int x, int y) {
        return matrix[x][y] == DEATH;
    }




}
