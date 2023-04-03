package com.hqy.cloud.netty.websocket.util;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;

/**
 * ssl工具类
 * https://blog.csdn.net/wecloud1314/article/details/123042277
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/3/8 14:40
 */
public class SslUtil {

    private SslUtil() {}

    /**
     * 创建SSLContext
     * @param type 类型
     * @param path 证书存放路径
     * @param password 秘钥
     * @return
     * @throws Exception
     */
    public static SSLContext createSslContext(String type, String path, String password) throws Exception {
        //JKS
        KeyStore keyStore = KeyStore.getInstance(type);
        //获取证书位置的流
        InputStream inputStream = new FileInputStream(path);
        keyStore.load(inputStream, password.toCharArray());
        //KeyManagerFactory充当基于密钥内容源的密钥管理器的工厂
        //getDefaultAlgorithm:获取默认的 KeyManagerFactory 算法名称。
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(keyStore, password.toCharArray());
        //SSLContext的实例表示安全套接字协议的实现，它充当用于安全套接字工厂或 SSLEngine 的工厂。
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(keyManagerFactory.getKeyManagers(), null, null);
        return sslContext;
    }


}
