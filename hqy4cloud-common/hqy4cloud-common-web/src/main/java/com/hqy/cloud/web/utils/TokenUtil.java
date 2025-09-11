package com.hqy.cloud.web.utils;

/**
 * @author qiyuan.hong
 * @date 2024/7/18
 */
public class TokenUtil {

    private static final ThreadLocal<String> TOKEN_THREADLOCAL = new ThreadLocal<>();

    public static String get() {
        return TOKEN_THREADLOCAL.get();
    }

    public static String getAndRemove() {
        try {
            return TOKEN_THREADLOCAL.get();
        } finally {
            TOKEN_THREADLOCAL.remove();
        }
    }

    public static void remove() {
        TOKEN_THREADLOCAL.remove();
    }

    public static void set(String token) {
        TOKEN_THREADLOCAL.set(token);
    }




}
