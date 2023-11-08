package com.hqy.cloud.arithmetic.A;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/7/4 9:30
 */
public class YinHangPaiDui {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = Integer.parseInt(scanner.nextLine().trim());
        if (n < 1 || n > 500) {
            System.out.println(-1);
            return;
        }

        List<List<Customer>> events = new ArrayList<>();
        List<Customer> customers = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            String[] data = scanner.nextLine().trim().split(" ");
            if (data.length == 0) {
                continue;
            }
            String s = data[0];
            if (s.equals("a")) {
                customers.add(new Customer(Integer.parseInt(data[1]), Integer.parseInt(data[2])));
            } else {
                events.add(customers);
                customers = new ArrayList<>();
            }
        }

        if (events.isEmpty()) {
            System.out.println(-1);
            return;
        }

        for (List<Customer> event : events) {
            if (event.isEmpty()) {
                System.out.println(-1);
                continue;
            }
            event.sort(Comparator.comparingInt(e -> e.priority));
            System.out.println(event.get(0).num);
        }



    }


    private static class Customer {
        public int num;
        public int priority;

        public Customer(int num, int priority) {
            this.num = num;
            this.priority = priority;
        }

    }

}
