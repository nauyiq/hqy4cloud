package com.hqy.cloud.arithmetic.B;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/6/26 14:49
 */
public class GameMark {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        try {
            int[] data = Arrays.stream(scanner.nextLine().split(",")).mapToInt(Integer::parseInt).toArray();
            int judgeCount = data[0];
            int playerCount = data[1];

            if (judgeCount < 3 || judgeCount > 10 || playerCount < 3 || playerCount > 100) {
                throw new IllegalArgumentException();
            }

            Map<Integer, Player> players = new HashMap<>(playerCount);

            for (int i = 0; i < judgeCount; i++) {
                int[] comments = Arrays.stream(scanner.nextLine().split(",")).mapToInt(Integer::parseInt).toArray();
                for (int j = 0; j < comments.length; j++) {
                    int comment = comments[j];
                    if (comment < 1 || comment > 10) {
                        throw new IllegalArgumentException();
                    }
                    int id = j + 1;
                    Player player = players.containsKey(id) ? players.get(id) : new Player(id, new ArrayList<>());
                    List<Integer> scores = player.scores;
                    scores.add(comment);
                    players.put(id, player);
                }
            }

            Collection<Player> values = players.values();
            List<Player> collect = values.stream().sorted().collect(Collectors.toList());
            Collections.reverse(collect);
            System.out.println(collect.get(0).id + "," + collect.get(1).id + "," + collect.get(2).id);

        } catch (Exception e) {
            System.out.println(-1);
        }




    }

    public static class Player implements Comparable<Player> {
        public int id;
        public List<Integer> scores;


        public Player(int id, List<Integer> scores) {
            this.id = id;
            this.scores = scores;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Player player = (Player) o;
            return id == player.id;
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }

        @Override
        public int compareTo(Player o) {
            Collections.reverse(this.scores);
            Collections.reverse(o.scores);

            int sum1 = 0;
            int sum2 = 0;

            for (Integer score : this.scores) {
                sum1 += score;
            }
            for (Integer score : o.scores) {
                sum2 += score;
            }

            if (sum1 == sum2) {
                Map<Integer, List<Integer>> thisMap = this.scores.stream().collect(Collectors.groupingBy(score -> score));
                Map<Integer, List<Integer>> oMap = o.scores.stream().collect(Collectors.groupingBy(score -> score));

                for (int i = 10; i > 0; i--) {
                    int thisCount = thisMap.getOrDefault(i, new ArrayList<>()).size();
                    int oCount = oMap.getOrDefault(i, new ArrayList<>()).size();

                    if (thisCount != oCount) {
                        return thisCount - oCount;
                    }
                }

                return 0;
            } else {
                return sum1 - sum2;
            }

        }
    }




}
