package com.corundumstudio.socketio.ex;

import cn.hutool.core.util.StrUtil;
import com.corundumstudio.socketio.BroadcastOperations;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.handler.ClientsBoxEx;
import com.corundumstudio.socketio.messages.HttpErrorMessage;
import com.hqy.cloud.common.base.lang.StringConstants;
import com.hqy.cloud.common.swticher.CommonSwitcher;
import com.hqy.cloud.util.AssertUtil;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * @author qiyuan.hong
 * @date 2022-03-18
 */
public class SocketIoUtil {

    private static final Logger log = LoggerFactory.getLogger(SocketIoUtil.class);

    private SocketIoUtil() {}

    public static boolean isInnerIp(String ip) {
        return StringConstants.INNER_IP.equals(ip);
    }


    public static HttpErrorMessage createHttpErrorMessage(int code, String message) {
        Map<String, Object> map = new HashMap<>(2);
        map.put("code", code);
        map.put("message", message);
        return new HttpErrorMessage(map);
    }

    /**
     * 推送消息给客户端
     * @param bizId          发给谁
     * @param eventName      事件名
     * @param wsMessageJson  事件的json数据
     * @return 结果
     */
    public static boolean doPush(String bizId, String eventName, String wsMessageJson, SocketIOServer socketIOServer) {
        log.info("@@@ doPush message, bizId -> {}, eventName -> {}, wsMessageJson -> {}", bizId, eventName, wsMessageJson);
        if (org.apache.commons.lang3.StringUtils.isAnyBlank(bizId, eventName)) {
            return false;
        }
        //bizId用户与服务端简历的长连接UUID集合, 即channel的UUID
        Set<UUID> uuidSet = ClientsBoxEx.getInstance().getUUID(bizId);
        if (CollectionUtils.isEmpty(uuidSet)) {
            //表示通知人不在线了. 则无需发通知了
            log.info("@@@ Message receiver is offline, eventName = {}, to = {}", eventName, bizId);
        } else {
            uuidSet.forEach(uuid -> {
                SocketIOClient client = socketIOServer.getClient(uuid);
                if (Objects.nonNull(client) && client.isChannelOpen()) {
                    client.sendEvent(eventName, wsMessageJson);
                }
            });
        }
        return true;
    }

    /**
     * 推送消息给所有的客户端
     * @param eventName       事件名
     * @param eventJsonData   事件json
     * @param socketIOServer  推送的服务端
     */
    public static void doPushAll(String eventName, String eventJsonData, SocketIOServer socketIOServer) {
        AssertUtil.notNull(socketIOServer, "Socketio server should not be null.");
        BroadcastOperations operations = socketIOServer.getBroadcastOperations();
        operations.sendEvent(eventName, eventJsonData);
        if (CommonSwitcher.JUST_4_TEST_DEBUG.isOn()) {
            log.info("Broadcast message {}, content {}.", eventName, eventJsonData);
        }
    }

    /**
     * 允许跨域，回写跨域相关响应头
     * @param httpResponse httpResponse
     */
    public static void allowCors(HttpResponse httpResponse, String origin) {
        if (StrUtil.isBlank(origin) || origin.equals(StringConstants.NULL)) {
            httpResponse.headers().set("access-control-allow-origin", "*");
        } else {
            httpResponse.headers().set("access-control-allow-origin", origin);
        }
        httpResponse.headers().set("Access-Control-Allow-Credentials",true);
        httpResponse.headers().add("Access-Control-Allow-Headers", "*");
        httpResponse.headers().add("Referrer-Policy","origin-when-cross-origin");
        httpResponse.headers().add("X-Frame-Options", "sameorigin");
    }


    /**
     * 解析request中的ip
     * @param request FullHttpRequest
     * @return 用户真是ip
     */
    public static String getRequestIp(FullHttpRequest request) {
        //一些 CDN 会在 CDN 回源请求加 HTTP 头信息，其中可能包含访客的“真实 IP ”。具体要看是哪家 CDN ，比如 Cloudflare 是 CF-Connecting-IP
        String cfIp = request.headers().get("cf-connecting-ip");
        if (!isInnerIp(cfIp) && StringUtils.hasText(cfIp) && !StringConstants.UNKNOWN.equalsIgnoreCase(cfIp)) {
            return cfIp;
        }
        //一些 CDN 会在 CDN 回源请求加 HTTP 头信息，其中可能包含访客的“真实 IP ”。具体要看是哪家 CDN ，比如 Cloudflare 是 CF-Connecting-IP
        String srcIp = request.headers().get("X-Real-IP");
        if (!isInnerIp(srcIp) && StringUtils.hasText(srcIp) && !StringConstants.UNKNOWN.equalsIgnoreCase(srcIp)) {
            return srcIp;
        }

        srcIp = request.headers().get("x-forwarded-for");
        if (StringUtils.hasText(srcIp) && !StringConstants.UNKNOWN.equalsIgnoreCase(srcIp)) {
            String[] ips = srcIp.split(",");
            for (String ip : ips) {
                if (!isInnerIp(ip) && !"unknown".equalsIgnoreCase(ip)) {
                    return ip;
                }
            }
        }

        srcIp = request.headers().get("Proxy-Client-IP");
        if (!isInnerIp(srcIp) && StringUtils.hasText(srcIp) && !StringConstants.UNKNOWN.equalsIgnoreCase(srcIp)) {
            return srcIp;
        }

        srcIp = request.headers().get("WL-Proxy-Client-IP");
        if (!isInnerIp(srcIp) && StringUtils.hasText(srcIp) && !StringConstants.UNKNOWN.equalsIgnoreCase(srcIp)) {
            return srcIp;
        }

        srcIp = request.headers().get("HTTP_CLIENT_IP");
        if (!isInnerIp(srcIp) && StringUtils.hasText(srcIp) && !StringConstants.UNKNOWN.equalsIgnoreCase(srcIp)) {
            return srcIp;
        }

        srcIp = request.headers().get("HTTP_X_FORWARDED_FOR");
        if (!isInnerIp(srcIp) && StringUtils.hasText(srcIp) && !StringConstants.UNKNOWN.equalsIgnoreCase(srcIp)) {
            return srcIp;
        }

        return "";
    }



}
