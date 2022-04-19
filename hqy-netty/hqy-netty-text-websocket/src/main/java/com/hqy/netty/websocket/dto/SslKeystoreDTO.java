package com.hqy.netty.websocket.dto;

/**
 * 使用安全加密的时候ssl keystore
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/3/8 15:01
 */
public class SslKeystoreDTO {

    /**
     * 秘钥位置 存在默认值
     */
    public String keystoreFilePath = "C:/hqy/conf/ssl/keystore";

    /**
     * 秘钥
     */
    public String keystorePassword = "changeIt";

    /**
     * 秘钥类型 默认JKS
     */
    public String keystoreType = "JSK";


    public SslKeystoreDTO() {
    }

    public SslKeystoreDTO(String keystoreFilePath, String keystorePassword, String keystoreType) {
        this.keystoreFilePath = keystoreFilePath;
        this.keystorePassword = keystorePassword;
        this.keystoreType = keystoreType;
    }

    @Override
    public String toString() {
        return "SslKeystoreDTO{" +
                "keystoreFilePath='" + keystoreFilePath + '\'' +
                ", keystorePassword='" + keystorePassword + '\'' +
                ", keystoreType='" + keystoreType + '\'' +
                '}';
    }
}
