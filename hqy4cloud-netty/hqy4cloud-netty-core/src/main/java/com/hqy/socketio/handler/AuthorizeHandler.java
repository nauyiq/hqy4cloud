/**
 * Copyright (c) 2012-2019 Nikita Koksharov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hqy.socketio.handler;

import com.hqy.cloud.common.result.CommonResultCode;
import com.hqy.cloud.common.swticher.CommonSwitcher;
import com.hqy.ex.NettyContextHelper;
import com.hqy.socketio.*;
import com.hqy.socketio.ack.AckManager;
import com.hqy.socketio.namespace.Namespace;
import com.hqy.socketio.namespace.NamespacesHub;
import com.hqy.socketio.protocol.AuthPacket;
import com.hqy.socketio.protocol.Packet;
import com.hqy.socketio.protocol.PacketType;
import com.hqy.socketio.scheduler.CancelableScheduler;
import com.hqy.socketio.scheduler.SchedulerKey;
import com.hqy.socketio.store.StoreFactory;
import com.hqy.socketio.store.pubsub.ConnectMessage;
import com.hqy.socketio.store.pubsub.PubSubType;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

@Sharable
public class AuthorizeHandler extends ChannelInboundHandlerAdapter implements Disconnectable {

    private static final Logger log = LoggerFactory.getLogger(AuthorizeHandler.class);

    private final CancelableScheduler disconnectScheduler;

    private final String connectPath;
    private final Configuration configuration;
    private final NamespacesHub namespacesHub;
    private final StoreFactory storeFactory;
    private final DisconnectableHub disconnectable;
    private final AckManager ackManager;
    private final ClientsBox clientsBox;

    public AuthorizeHandler(String connectPath, CancelableScheduler scheduler, Configuration configuration, NamespacesHub namespacesHub, StoreFactory storeFactory,
            DisconnectableHub disconnectable, AckManager ackManager, ClientsBox clientsBox) {
        super();
        this.connectPath = connectPath;
        this.configuration = configuration;
        this.disconnectScheduler = scheduler;
        this.namespacesHub = namespacesHub;
        this.storeFactory = storeFactory;
        this.disconnectable = disconnectable;
        this.ackManager = ackManager;
        this.clientsBox = clientsBox;
    }

    @Override
    public void channelActive(final ChannelHandlerContext ctx) throws Exception {
        SchedulerKey key = new SchedulerKey(SchedulerKey.Type.PING_TIMEOUT, ctx.channel());
        disconnectScheduler.schedule(key, new Runnable() {
            @Override
            public void run() {
                ctx.channel().close();
                log.debug("Client with ip {} opened channel but doesn't send any data! Channel closed!", ctx.channel().remoteAddress());
            }
        }, configuration.getFirstDataTimeout(), TimeUnit.MILLISECONDS);
        super.channelActive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        SchedulerKey key = new SchedulerKey(SchedulerKey.Type.PING_TIMEOUT, ctx.channel());
        disconnectScheduler.cancel(key);

        if (msg instanceof FullHttpRequest) {
            FullHttpRequest req = (FullHttpRequest) msg;
            Channel channel = ctx.channel();
            QueryStringDecoder queryDecoder = new QueryStringDecoder(req.uri());
            //origin
            String origin = req.headers().get(HttpHeaderNames.ORIGIN);

            if (!configuration.isAllowCustomRequests()
                    && !queryDecoder.path().startsWith(connectPath)) {
                if (CommonSwitcher.ENABLE_GATEWAY_SOCKET_AUTHORIZE.isOn()) {
                    //当接入Gateway时, 源码在此校验握手数据失败时会将连接断开. 这时候Gateway将抛出Connection prematurely closed DURING response异常,即连接提前关闭了 网关还未接收到相应
                    //并且直接往通道里写入HttpErrorMessage对象 交给EncoderHandler去处理异常消息。
                    origin = StringUtils.isBlank(origin) ? "null" : origin;
                    channel.attr(EncoderHandler.ORIGIN).set(origin);
                    channel.writeAndFlush(NettyContextHelper.createHttpErrorMessage(0, "error connectPath."));
                } else {
                    HttpResponse res = new DefaultHttpResponse(HTTP_1_1, HttpResponseStatus.UNAUTHORIZED);
                    channel.writeAndFlush(res).addListener(ChannelFutureListener.CLOSE);
                }
                req.release();
                return;
            }

            List<String> sid = queryDecoder.parameters().get("sid");
            if (queryDecoder.path().equals(connectPath)
                    && sid == null) {

                if (!authorize(ctx, channel, origin, queryDecoder.parameters(), req)) {
                    req.release();
                    return;
                }
                // forward message to polling or websocket handler to bind channel
            }
        }
        ctx.fireChannelRead(msg);
    }

    private boolean authorize(ChannelHandlerContext ctx, Channel channel, String origin, Map<String, List<String>> params, FullHttpRequest req)
            throws IOException {
        Map<String, List<String>> headers = new HashMap<>(req.headers().names().size());
        for (String name : req.headers().names()) {
            List<String> values = req.headers().getAll(name);
            headers.put(name, values);
        }
        HandshakeData data = new HandshakeData(req.headers(), params,
                (InetSocketAddress)channel.remoteAddress(),
                (InetSocketAddress)channel.localAddress(),
                req.uri(), origin != null && !"null".equalsIgnoreCase(origin));
        //获取客户端真实ip
        String requestIp = NettyContextHelper.getRequestIp(req);
        data.setRealIp(requestIp);
        log.info("@@@ [准备校验握手数据] ip:{}, userAgent:{}", requestIp, data.getUserAgent());

        boolean result = false;
        try {
            result = configuration.getAuthorizationListener().isAuthorized(data);
        } catch (Exception e) {
            log.error("Authorization error, realIp:{}", requestIp, e);
        }
        if (!result) {
            if (CommonSwitcher.ENABLE_GATEWAY_SOCKET_AUTHORIZE.isOn()) {
                //当接入Gateway时, 源码在此校验握手数据失败时会将连接断开. 这时候Gateway将抛出Connection prematurely closed DURING response异常,即连接提前关闭了 网关还未接收到相应
                //并且直接往通道里写入HttpErrorMessage对象 交给EncoderHandler去处理异常消息。
                channel.attr(EncoderHandler.ORIGIN).set(origin);
                channel.writeAndFlush(NettyContextHelper.
                        createHttpErrorMessage(CommonResultCode.INVALID_ACCESS_TOKEN.code, CommonResultCode.INVALID_ACCESS_TOKEN.message));
                return false;
            } else {
                HttpResponse res = new DefaultHttpResponse(HTTP_1_1, HttpResponseStatus.UNAUTHORIZED);
                NettyContextHelper.allowCors(res, data.getOrigin());
                channel.writeAndFlush(res).addListener(ChannelFutureListener.CLOSE);
            }
            log.debug("Handshake unauthorized, query params: {} headers: {} realIp:{}", params, headers, requestIp);
            return false;
        }
        UUID sessionId;
        if (configuration.isRandomSession()) {
            sessionId = UUID.randomUUID();
        } else {
            sessionId = this.generateOrGetSessionIdFromRequest(req.headers());
        }
        if (StringUtils.isBlank(data.getBizId())) {
            data.setBizId(sessionId.toString());
        }

        List<String> transportValue = params.get("transport");
        if (transportValue == null) {
            log.error("Got no transports for request {}", req.uri());
            if (CommonSwitcher.ENABLE_GATEWAY_SOCKET_AUTHORIZE.isOn()) {
                //当接入Gateway时, 源码在此校验握手数据失败时会将连接断开. 这时候Gateway将抛出Connection prematurely closed DURING response异常,即连接提前关闭了 网关还未接收到相应
                channel.attr(EncoderHandler.ORIGIN).set(origin);
                channel.writeAndFlush(NettyContextHelper.createHttpErrorMessage(0, "Got no transports for request."));
            } else {
                //返回response并且关闭连接.
                HttpResponse res = new DefaultHttpResponse(HTTP_1_1, HttpResponseStatus.UNAUTHORIZED);
                channel.writeAndFlush(res).addListener(ChannelFutureListener.CLOSE);
            }
            return false;
        }

        Transport transport = Transport.byName(transportValue.get(0));
        if (!configuration.getTransports().contains(transport)) {
            channel.attr(EncoderHandler.ORIGIN).set(origin);
            channel.writeAndFlush(NettyContextHelper.createHttpErrorMessage(0, "Transport unknown"));
            return false;
        }
        //channel.remoteAddress().toString(): 为了不持有此channel的属性对象，导致后续不释放。
        ClientHead client = new ClientHead(sessionId, ackManager, disconnectable, storeFactory, data,
                clientsBox, transport, disconnectScheduler, configuration, channel.remoteAddress().toString());
        channel.attr(ClientHead.CLIENT).set(client);
        clientsBox.addClient(client);
        if (CommonSwitcher.SOCKET_POLLING_HANDSHAKE_DATA_LEAK.isOn()) {
            //新增非法操作监听
            clientsBox.addClient(client, disconnectScheduler, configuration, namespacesHub);
        } else {
            clientsBox.addClient(client);
        }

        String[] transports = {};
        if (configuration.getTransports().contains(Transport.WEBSOCKET)) {
            transports = new String[]{"websocket"};
        }

        AuthPacket authPacket = new AuthPacket(sessionId, transports, configuration.getPingInterval(),
                configuration.getPingTimeout());
        Packet packet = new Packet(PacketType.OPEN);
        //第一个接口返回的数据类型--包括 sid ,只是将数据放到队列上，还没进行真正意义上的推送。
        packet.setData(authPacket);
        //需要等到下一个环节的：polling通道 绑定bind  --触发在：clientHead的生命流程中的绑定
        client.send(packet);
        client.schedulePingTimeout();
        log.debug("Handshake authorized for sessionId: {}, query params: {} headers: {}", sessionId, params, headers);
        return true;
    }

    /**
     * This method will either generate a new random sessionId or will retrieve the value stored
     * in the "io" cookie.  Failures to parse will cause a logging warning to be generated and a
     * random uuid to be generated instead (same as not passing a cookie in the first place).
     */
    private UUID generateOrGetSessionIdFromRequest(HttpHeaders headers) {
        List<String> values = headers.getAll("io");
        if (values.size() == 1) {
            try {
                return UUID.fromString(values.get(0));
            } catch (IllegalArgumentException iaex) {
                log.warn("Malformed UUID received for session! io=" + values.get(0));
            }
        }

        for (String cookieHeader : headers.getAll(HttpHeaderNames.COOKIE)) {
            Set<Cookie> cookies = ServerCookieDecoder.LAX.decode(cookieHeader);

            for (Cookie cookie : cookies) {
                if ("io".equals(cookie.name())) {
                    try {
                        return UUID.fromString(cookie.value());
                    } catch (IllegalArgumentException iaex) {
                        log.warn("Malformed UUID received for session! io=" + cookie.value());
                    }
                }
            }
        }

        return UUID.randomUUID();
    }

    public void connect(UUID sessionId) {
        SchedulerKey key = new SchedulerKey(SchedulerKey.Type.PING_TIMEOUT, sessionId);
        disconnectScheduler.cancel(key);
    }

    /**
     * 兼容直连的websocket业务
     * @param client ClientHead
     * @param polling boolean
     * @return Namespace
     */
    public Namespace connect(ClientHead client, boolean polling) {
        Namespace ns = namespacesHub.get(Namespace.DEFAULT_NAME);
        if (!client.getNamespaces().contains(ns)) {
            Packet packet = new Packet(PacketType.MESSAGE);
            packet.setSubType(PacketType.CONNECT);
            client.send(packet);
            configuration.getStoreFactory().pubSubStore().publish(PubSubType.CONNECT, new ConnectMessage(client.getSessionId()));
            client.addNamespaceClient(ns);
            if (polling) {
                log.info("@@@ Polling, ignore ns.onConnect.");
            }
//            ns.onConnect(nsClient);
        }
        return ns;
    }

    @Override
    public void onDisconnect(ClientHead client) {
        clientsBox.removeClient(client.getSessionId());
    }

}
