package com.hqy.cloud.chatgpt.common.lang;

/**
 * 使用官方API or ChatGPTUnofficialProxyAPI(使用access token请求大佬的反向代理)
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/7/27 10:27
 */
public enum ApiType {

    /**
     * api key收费 收费标准为1token = 0.00012
     * 官方API 需要注意的事 国内有没用梯子的情况是不允许直接请求gpt-api的
     */
    API("ChatGPTAPI"),

    /**
     * 免费，需要将authorization暴露给第三方，自己斟酌.. 并且目前可能代理服务器不够稳定.
     * 通过access token请求第三方代理服务器 在反向代理open ai
     */
    REVERSE("ChatGPTUnofficialProxyAPI")

    ;

    public final String name;


    ApiType(String name) {
        this.name = name;
    }




}
