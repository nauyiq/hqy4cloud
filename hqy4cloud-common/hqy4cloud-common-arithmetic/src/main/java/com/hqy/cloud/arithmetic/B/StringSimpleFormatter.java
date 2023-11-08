package com.hqy.cloud.arithmetic.B;


import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/6/19 16:25
 */
public class StringSimpleFormatter {

    static List<String> characters = new LinkedList<>();
    static String input;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        input = scanner.nextLine();
        if (input.length() == 0) {
            System.out.println(0);
            return;
        }

        List<String> result = new LinkedList<>();

        if (input.contains("(")) {
            int lastIndex = 0;
            while (lastIndex != -1) {
                int index = input.indexOf('(', lastIndex);
                if (index != -1) {
                    expectLeft(index, lastIndex, result);
                    lastIndex = index + 1;
                } else {
                    lastIndex = -1;
                }
            }
        } else {
            System.out.println(input);
            return;
        }


        if (result.isEmpty()) {
            System.out.println(0);
            return;
        }

        characters = characters.stream().sorted().collect(Collectors.toList());
        String resultString = "";
        for (String s : result) {
            resultString = resultString.concat(s);
        }

        for (int i = 0; i < resultString.length(); i++) {
            char charAt = resultString.charAt(i);
            String value = String.valueOf(charAt);
            if (characters.contains(value.toLowerCase()) || characters.contains(value.toUpperCase())) {
                String s = characters.get(0);
                if (i == 0) {
                    resultString = s + resultString.substring(1);
                } else {
                    resultString = resultString.substring(0, i) + s + resultString.substring(i + 1);
                }
            }
        }


        System.out.println(resultString);

    }

    public static void expectLeft(int index, int lastIndex, List<String> result) {
        int indexLeft = input.indexOf("(", index + 1);
        int indexRight = input.indexOf(")", index + 1);
        // 说明两个'('一起出现， 不存在等效字符
        if (indexRight > indexLeft && indexLeft != -1) {
            String substring = input.substring(index + 1, indexLeft);
            result.add(substring);
        } else if (indexLeft == -1 && indexRight == -1) {
            result.add(input.substring(index + 1));
        } else if (indexRight > input.lastIndexOf("(") && indexRight < input.length() - 1) {
            result.add(input.substring(indexRight + 1));
        } else {
            String substring = input.substring(index + 1, indexRight);
            if (substring.length() > 1) {
                for (int i = 0; i < substring.length(); i++) {
                    String charAt = String.valueOf(substring.charAt(i));
                    if (!characters.contains(charAt.toLowerCase()) && !characters.contains(charAt.toUpperCase())) {
                        characters.add(charAt);
                    }
                }
            }

            int i = input.indexOf(")", lastIndex);
            if (i < index) {
                result.add(input.substring(i + 1, index));
            }

        }





    }



}
