package com.hqy.cloud.socketio.test;

import io.socket.client.IO;
import io.socket.client.Socket;

import java.net.URISyntaxException;
import java.util.Arrays;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/3 10:00
 */
public class Main {

    public static void main(String[] args) {
        IO.Options options = new IO.Options();
        options.path = "/blog/websocket";
        options.transports = new String[]{"websocket"};
        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHAiOjE2OTEwMjk4Nzc3NjgsInBheWxvYWQiOiJ7XCJhcHBcIjp7XCJhcHBcIjpcImhxeTRjbG91ZC1hcHBzLWJsb2ctc2VydmljZVwiLFwidmVyc2lvblwiOlwiMS4wLjBcIn0sXCJiaXpJZFwiOlwiaG9uZ3F5XCIsXCJjcmVhdGVUaW1lXCI6MTY5MTAyNjI3NzQ5MH0ifQ.M_Yi0Xa9cI2CO4a3LKF5lN3Ma-broH3J7WXMXArKYXU";

        try {
            final Socket socket = IO.socket("http://172.16.42.73:9527/blog/websocket/" + "?Authorization=" + token , options);
            Socket connect = socket.connect();
            connect.on(Socket.EVENT_CONNECT, objects -> {
                System.out.println(Arrays.toString(objects));
            });
            if (connect.connected()) {
                System.out.println("连接成功");
            }

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }


    }

}
