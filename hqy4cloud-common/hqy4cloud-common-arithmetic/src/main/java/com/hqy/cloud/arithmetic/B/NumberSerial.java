package com.hqy.cloud.arithmetic.B;

import java.util.*;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/6/21 9:20
 */
public class    NumberSerial {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        String nextLine = scanner.nextLine();
        String[] data = nextLine.split(" ");
        if (data.length != 2) {
            return;
        }

        String numbers = data[0];
        int count = Integer.parseInt(data[1]);

        List<Character> inputNumbersCharacterList = new ArrayList<>();
        for (int i = 0; i < numbers.length(); i++) {
            inputNumbersCharacterList.add(numbers.charAt(i));
        }

        boolean success = false;
        Map<Character, Integer> characterIntegerMap = new HashMap<>();
        for (int i = 0; i < 1000 - count; i++) {
            characterIntegerMap = getCurrentWindowCharacterList(i, count);
            int length = getCurrentWindowLength(i, count);
            if (checkIsSameCharacter(inputNumbersCharacterList, characterIntegerMap, length)) {
                success = true;
                break;
            }
        }

        if (success) {
            Collection<Integer> values = characterIntegerMap.values();
            System.out.println(Collections.min(values));
        }

    }

    private static int getCurrentWindowLength(int index, int count) {
        String data = "";
        for (int i = index; i < index + count; i++) {
            data = data.concat(String.valueOf(i));
        }
        return data.length();
    }


    private static Map<Character, Integer> getCurrentWindowCharacterList(int index, int count) {
        Map<Character, Integer> map = new HashMap<>();
        for (int i = index; i < index + count; i++) {
            String of = String.valueOf(i);
            for (int i1 = 0; i1 < of.length(); i1++) {
                char charAt = of.charAt(i1);
                if (map.containsKey(charAt)) {
                    Integer integer = map.get(charAt);
                    if (integer < i) {
                        map.put(of.charAt(i1), i);
                    }
                } else {
                    map.put(of.charAt(i1), i);
                }
            }
        }

        return map;
    }

    private static boolean checkIsSameCharacter(List<Character> origin,  Map<Character, Integer> current, int currentLength) {
        if (origin.size() != currentLength) {
            return false;
        }

        for (Character character : current.keySet()) {
            if (!origin.contains(character)) {
                return false;
            }
        }


        return true;
    }



}
