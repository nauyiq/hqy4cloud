package com.hqy.cloud.arithmetic.A;

import java.util.*;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/6/30 9:35
 */
public class BullsCows {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = Integer.parseInt(scanner.nextLine().trim());
        if (n < 0 || n > 100) {
            System.out.println("NA");
            return;
        }

        Map<String, int[]> map = new HashMap<>(n);

        for (int i = 0; i < n; i++) {
            String[] strings = scanner.nextLine().trim().split(" ");
            if (strings.length != 2) {
                System.out.println("NA");
                return;
            }
            String number = strings[0];
            String hint = strings[1];
            if (number.length() != 4 && hint.length() != 4) {
                System.out.println("NA");
                return;
            }

            int count_a = Integer.parseInt(hint.substring(0, 1));
            int count_b = Integer.parseInt(hint.substring(2, 3));
            int[] hints = {count_a, count_b};
             map.put(number, hints);
        }

        List<Integer> result = new ArrayList<>();
        // 因为为四位数. 遍历答案即可
        for (int i = 1000; i < 10000; i++) {
            if (i == 3585) {
                System.out.println();
            }

            if (check(i, map)) {
                if (result.isEmpty()) {
                    result.add(i);
                } else {
                    break;
                }
            }
        }
        if (result.isEmpty()) {
            System.out.println("NA");
        } else {
            System.out.println(result.get(0));
        }
    }

    private static boolean check(int i, Map<String, int[]> map) {
        String answer = i + "";
        List<Character> answerList = new ArrayList<>();
        for (int j = 0; j < answer.length(); j++) {
            answerList.add(answer.charAt(j));
        }


        for (Map.Entry<String, int[]> entry : map.entrySet()) {
            String number = entry.getKey();
            int a = entry.getValue()[0];
            int b = entry.getValue()[1];

            if (a == 0 && b == 0) {
                for (int j = 0; j < number.length(); j++) {
                    char charAt = number.charAt(j);
                    if (charAt == answer.charAt(j) || answerList.contains(charAt)) {
                        return false;
                    }
                }
            } else if (a > 0 && b == 0) {
                int aCount = 0;
                for (int j = 0; j < number.length(); j++) {
                    char charAt = number.charAt(j);
                    if (answerList.get(j).equals(charAt)) {
                        aCount++;
                    }
                }
                if (aCount != a) {
                    return false;
                }
            } else if (a == 0 && b > 0) {
                int bCount = 0;
                for (int j = 0; j < number.length(); j++) {
                    char charAt = number.charAt(j);
                    if (charAt == answer.charAt(j)) {
                        return false;
                    }
                    if (answerList.contains(charAt)) {
                        bCount++;
                    }
                }
                if (bCount != b) {
                    return false;
                }
            } else {
                int aCount = 0;
                int bCount = 0;
                List<Character> newList = new ArrayList<>();
                List<Character> aList = new ArrayList<>();
                for (int j = 0; j < number.length(); j++) {
                    char charAt = number.charAt(j);
                    if (answerList.get(j).equals(charAt)) {
                        aCount++;
                        aList.add(charAt);
                    }
                }
                if (aCount != a) {
                    return false;
                }


                for (int j = 0; j < number.length(); j++) {
                    char charAt = number.charAt(j);
                    if (!aList.contains(charAt)) {
                        newList.add(charAt);
                    }
                }

                for (Character character : newList) {
                    if (answerList.contains(character)) {
                        bCount++;
                    }
                }
                if (bCount != b) {
                    return false;
                }
            }

        }

        return true;
    }





}
