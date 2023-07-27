package com.hqy.cloud.arithmetic.A;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/7/14 17:59
 */
public class LikelyString {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = Integer.parseInt(scanner.nextLine());
        String[] arrays = new String[n];
        for (int i = 0; i < n; i++) {
            arrays[i] = scanner.nextLine();
        }
        String checked = scanner.nextLine();

        dfs(arrays, checked);

        result = LikelyString.result.stream().distinct().collect(Collectors.toList());
        if (result.isEmpty()) {
            System.out.println("null");
        }

        result.sort((s1, s2) -> {
            int min = Math.min(s1.length(), s2.length());
            for (int i = 0; i < min; i++) {
                char charAt1 = s1.charAt(i);
                char charAt2 = s2.charAt(i);
                if (charAt1 != charAt2) {
                    return charAt1 - charAt2;
                }

            }
            return 0;
        });

        String output = "";
        for (String s : result) {
            output += s + " ";
        }

        System.out.println(output);

    }

    private static List<String> result = new ArrayList<>();

    private static void dfs(String[] arrays, String checked) {
        for (String current : arrays) {
            if (check(current, checked)) {
                result.add(current);
            }
        }
    }

    private static boolean check(String array, String next) {
        if (array.length() != next.length()) {
            return false;
        }

        char[] charArray = array.toCharArray();
        char[] charNext = next.toCharArray();

        Arrays.sort(charArray);
        Arrays.sort(charNext);

        for (int i = 0; i < array.length(); i++) {
            if (charArray[i] != charNext[i]) {
                return false;
            }
        }

        return true;
    }








}
