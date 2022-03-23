package com.hqy.ex;

import java.io.Serializable;

/**
 * socket项目上下文定义
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/3/23 15:34
 */
public class SocketProjectContext implements Serializable {

    private static final long serialVersionUID = 9112836802881408555L;

    /**
     * app信息
     */
    private App app;

    /**
     * 业务id 通常为唯一标识 比如说用户的邮箱, 用户的id, 手机等
     */
    private String bizId;

    /**
     * 创建时间
     */
    private Long createTime;


    public SocketProjectContext() {
    }

    public SocketProjectContext(App app, String bizId) {
        this.app = app;
        this.bizId = bizId;
        this.createTime = System.currentTimeMillis();
    }

    public App getApp() {
        return app;
    }

    public void setApp(App app) {
        this.app = app;
    }

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public static class App {
        /**
         * app名字
         */
        private String app;

        /**
         * 版本
         */
        private String version;

        public App() {
        }

        public App(String app) {
            this.app = app;
            this.version = "1.0";
        }

        public App(String app, String version) {
            this.app = app;
            this.version = version;
        }

        public String getApp() {
            return app;
        }

        public void setApp(String app) {
            this.app = app;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }
    }


}
