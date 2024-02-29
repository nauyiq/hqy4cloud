package com.hqy.cloud.chatgpt.common.lang;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/7/27 11:32
 */
public enum ChatGptModel {

    /**
     * gpt-3.5-turbo
     */
    GTP_3_5_TURBO("gpt-3.5-turbo"),


    ;

    public final String name;


    ChatGptModel(String name) {
        this.name = name;
    }


}
