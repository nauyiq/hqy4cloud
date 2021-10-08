package com.hqy.rpc.nacos;

/**
 * @author qy
 * @project: hqy-parent-all
 * @create 2021-10-08 17:38
 */
public class NacosNodeUtil {

    private static NacosNode node;

    private static

    private static NacosNodeUtil instance = null;

    public static NacosNodeUtil getInstance() {
        if (instance == null) {
            synchronized (NacosNodeUtil.class) {
                if (instance == null) {
                    instance = new NacosNodeUtil();
                }
            }
        }
        return instance;
    }


    public NacosNode concurrentNode() {
        return node;
    }


    public void buildNodeInfo(int pubValue, String nameEn, int port, int usingPort, XxNode xxNode) {

    }




}
