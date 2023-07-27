package com.hqy.cloud.arithmetic.B;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/6/26 16:21
 */
public class CommonSuffix {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String line = scanner.nextLine();
        line = line.replace("[", "").replace("]", "").replace("\"", "").
                replace(",", " ").trim();
        String[] data = line.split(" ");

        Map<Integer, List<Character>> map = new HashMap<>();
        List<Character> commonCharacters = new ArrayList<>();

        int i = 1;
        char lastChar = 0;
        while (i != -1) {
            for (String input : data) {
                if (i > input.length()) {
                    break;
                }
                lastChar = input.charAt(input.length() - i);
                List<Character> characters = map.computeIfAbsent(i, v -> new ArrayList<>());
                characters.add(lastChar);
            }

            List<Character> characters = map.get(i);
            if (characters.size() == data.length) {
                characters = characters.stream().distinct().collect(Collectors.toList());
                if (characters.size() == 1) {
                    commonCharacters.add(lastChar);
                    i++;
                } else {
                    i = -1;
                }
            } else {
                i = -1;
            }


        }

        if (commonCharacters.isEmpty()) {
            System.out.println("@Zero");
            return;
        }

        Stack<Character> stack = new Stack<>();
        for (Character character : commonCharacters) {
            stack.push(character);
        }

        String output = "";
        while (!stack.empty()) {
            output = output.concat(stack.pop().toString());
        }

        System.out.println(output);


    }


}
