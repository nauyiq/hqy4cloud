package com.hqy.rpc.nacos;

import cn.hutool.core.lang.UUID;
import com.hqy.rpc.regist.GreyWhitePub;
import com.hqy.rpc.regist.Node;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

/**
 * 定制化服务节点信息
 * @author qy
 * @project: hqy-parent-all
 * @create 2021-08-13 10:21
 */
@Data
@AllArgsConstructor
public class ServerNode extends Node {

    /**
     * 节点id - uuid
     */
    private String nodeId;

    /**
     * 服务的第二个端口， 通道是websocket服务暴露的端口
     */
    private int port2;

    /**
     * 当前节点的hash值
     */
    private Integer hash;

    /**
     * 节点创建时间
     */
    private Date created;

    /**
     * 灰白度 默认灰度发布
     */
    private GreyWhitePub pub;


    public ServerNode() {
        this.created = new Date();
        pub = GreyWhitePub.GRAY;
        this.nodeId = UUID.randomUUID().toString();
    }
}
