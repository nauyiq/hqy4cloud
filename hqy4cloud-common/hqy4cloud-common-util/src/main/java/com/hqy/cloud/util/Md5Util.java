package com.hqy.cloud.util;

import com.auth0.jwt.internal.org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Md5加密工具类
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/3/11 16:51
 */
public class Md5Util {

    private static final Logger log = LoggerFactory.getLogger(Md5Util.class);

    private static final String SALT = "qaJs4D7FdQLBag7x";

    private Md5Util() {
    }

    /**
     * 普通的md5加密串
     * @param str 待加密字符串
     * @return md5的加密串
     */
    public static String getStrMd5(String str) {
        // 获取MD5实例
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            log.error(e.toString(), e);
            return "";
        }

        // 将加密字符串转换为字符数组
        char[] charArray = str.toCharArray();
        byte[] byteArray = new byte[charArray.length];

        // 开始加密
        for (int i = 0; i < charArray.length; i++) {
            byteArray[i] = (byte) charArray[i];
        }
        byte[] digest = md5.digest(byteArray);
        StringBuilder sb = new StringBuilder();
        for (byte b : digest) {
            int var = b & 0xff;
            if (var < 16) {
                sb.append("0");
            }
            sb.append(Integer.toHexString(var));
        }
        return sb.toString();
    }

    /**
     * 使用md5 + 盐进行加密
     * @param password 加密密码
     * @param salt 盐
     * @return
     */
    public static String getSaltMd5(String password, String salt) {
        if (StringUtils.isBlank(salt)) {
            salt = SALT;
        }
        password = md5Hex(password + salt);
        char[] cs = new char[48];
        for (int i = 0; i < 48; i += 3) {
            cs[i] = password.charAt(i / 3 * 2);
            char c = salt.charAt(i / 3);
            cs[i + 1] = c;
            cs[i + 2] = password.charAt(i / 3 * 2 + 1);
        }
        return String.valueOf(cs);
    }


    public static boolean checkSaltVerifyMd5(String password, String dbPassword) {
        char[] cs1 = new char[32];
        char[] cs2 = new char[16];
        for (int i = 0; i < 48; i += 3) {
            cs1[i / 3 * 2] = dbPassword.charAt(i);
            cs1[i / 3 * 2 + 1] = dbPassword.charAt(i + 2);
            cs2[i / 3] = dbPassword.charAt(i + 1);
        }
        String salt = new String(cs2);
        return md5Hex(password + salt).equals(String.valueOf(cs1));
    }


    /**
     * 随机生成一个16位加密盐值
     * @return 盐
     */
    public static String getSalt() {
        return RandomStringUtils.randomAlphanumeric(16);
    }

    /**
      *使用Apache的Hex类实现Hex(16进制字符串和)和字节数组的互转
     * @param str
     * @return
     */
    private static String md5Hex(String str) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(str.getBytes());
            return new String(new Hex().encode(digest));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return "";
        }
    }

    public static void main(String[] args) {
        String hqy = getStrMd5("hqy");
        System.out.println(hqy);
    }


}
