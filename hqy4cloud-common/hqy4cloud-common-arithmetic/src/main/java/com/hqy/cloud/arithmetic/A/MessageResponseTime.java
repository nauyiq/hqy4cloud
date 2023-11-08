package com.hqy.cloud.arithmetic.A;

import java.util.*;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/6/30 15:07
 */
public class MessageResponseTime {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();

        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            int time = scanner.nextInt();
            int responseTime = scanner.nextInt();
            int realResponseTime;
            if (responseTime < 128) {
                realResponseTime = time + responseTime;
            } else {
                // responseTime 低四位
                int mant = responseTime & 0x0F;
                // responseTime 高5到7位
                int ext = (responseTime & 0x70) >> 4;
                realResponseTime = (mant | 0x10 ) << (ext + 3);
            }
            result.add(realResponseTime);
        }
        result.sort(Comparator.comparingInt(s -> s));

        System.out.println(result.get(0));

    }

}
