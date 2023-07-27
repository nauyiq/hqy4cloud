package com.hqy.cloud.arithmetic.A;

import java.util.*;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/7/14 16:38
 */
public class MicroServerTest {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        Map<Integer, Server> servers = new HashMap<>();
        for (int i = 0; i < n; i++) {
            int time = 0;
            Set<Integer> dependencies = new HashSet<>();
            for (int j = 0; j < n; j++) {
                int input = scanner.nextInt();
                if (i == j) {
                    time = input;
                } else if (input == 1) {
                    dependencies.add(j + 1);
                }
            }
            servers.put(i + 1, new Server(i + 1, time, dependencies));
        }

        int targetServer = scanner.nextInt();

        dfs(servers, targetServer, new HashSet<>());
        System.out.println(result);
    }

    private static int result = 0;

    private static void dfs(Map<Integer, Server> servers, int target, Set<Integer> temp) {
        Server server = servers.get(target);
        if (server == null) {
            return;
        }

        result += server.time;

        // 依赖的服务id
        for (Integer dependency : server.dependencies) {
            if (!temp.contains(dependency)) {
                Set<Integer> new_temp = new HashSet<>(temp);
                new_temp.add(target);
                dfs(servers, dependency, new_temp);
            }
        }

    }


    private static class Server {
        public int id;
        public int time;
        public Set<Integer> dependencies;

        public Server(int id, int time) {
            this.id = id;
            this.time = time;
        }

        public Server(int id, int time, Set<Integer> dependencies) {
            this.id = id;
            this.time = time;
            this.dependencies = dependencies;
        }
    }


}
