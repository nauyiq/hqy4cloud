package com.hqy.netty.websocket.handler;

import cn.hutool.core.date.DateUtil;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.hqy.cloud.common.base.lang.StringConstants;
import com.hqy.cloud.common.base.lang.exception.PublishedException;
import com.hqy.cloud.common.swticher.CommonSwitcher;
import com.hqy.foundation.common.enums.ExceptionType;
import com.hqy.foundation.spring.event.ExceptionCollActionEvent;
import com.hqy.netty.websocket.base.HandshakeData;
import com.hqy.netty.websocket.base.WsErrorReason;
import com.hqy.netty.websocket.base.enums.CloseCode;
import com.hqy.netty.websocket.base.enums.CloseScene;
import com.hqy.netty.websocket.base.enums.WsMessageType;
import com.hqy.netty.websocket.exception.DefaultExceptionListener;
import com.hqy.netty.websocket.exception.ExceptionListener;
import com.hqy.netty.websocket.session.BaseWsSession;
import com.hqy.netty.websocket.session.ChannelWsSessionManager;
import com.hqy.cloud.util.JsonUtil;
import com.hqy.cloud.util.spring.SpringContextHolder;
import com.hqy.cloud.util.thread.ParentExecutorService;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.CharsetUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * websocket处理的pipeline handler 基于RFC 6455文档开发;
 * https://datatracker.ietf.org/doc/rfc6455/
 *
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/3/8 15:30
 */
public class WebsocketHandler extends SimpleChannelInboundHandler<Object> {

    private static final Logger log = LoggerFactory.getLogger(WebsocketHandler.class);

    private final Class<? extends BaseWsSession> wsSessionClass;

    private WebSocketServerHandshaker webSocketServerHandshaker;

    private final ExceptionListener listener = new DefaultExceptionListener();

    public WebsocketHandler(Class<? extends BaseWsSession> wsSessionClass) {
        super();
        this.wsSessionClass = wsSessionClass;
    }

    private final Cache<String, Date> closeFrameProcess = CacheBuilder.newBuilder().maximumSize(10 * 1024).
            expireAfterWrite(1, TimeUnit.MINUTES).build();

    /**
     * 打印debug级别的日志
     * @param logs 需要输出的日志
     */
    public static void debugPrint(String... logs) {
        if (CommonSwitcher.JUST_4_TEST_DEBUG.isOn()) {
            log.debug("{}", Arrays.toString(logs));
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        try {
            //获取通道id
            String channelId = ctx.channel().id().toString();
            debugPrint("channelActive -> channelId", channelId);

            BaseWsSession wsSession = wsSessionClass.newInstance();
            wsSession.initialize(ctx);
            ChannelWsSessionManager.registry(channelId, wsSession);
        } catch (Exception e) {
            listener.exceptionCaught(ctx, e);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        BaseWsSession wsSession = null;
        try {
            String channelId = ctx.channel().id().toString();
            debugPrint("channelInactive -> channelId", channelId);

            Date closeFrameTime = closeFrameProcess.getIfPresent(channelId);
            if (Objects.isNull(closeFrameTime)) {
                //说明已经没有正常的发送close报文
                log.warn("[WebsocketHandler] 非正常的关闭websocket. 即没发送close报文. channelId:{}", channelId);
                //需要cancel ,防止连接泄露...
                wsSession = ChannelWsSessionManager.cancel(channelId);
                //手动断开
                wsSession.onClose(new WsErrorReason(CloseCode.GOING_AWAY, "channelInactive"), CloseScene.SCENE_CLIENT_END_DISCONNECT);
            } else {
                //已经在onClientDisconnect里处理过了close 的报文...
                wsSession = ChannelWsSessionManager.getSession(channelId);
                debugPrint("channelInactive", "客户端连接失效(正断)", JsonUtil.toJson(wsSession.getHandshakeData()), DateUtil.formatDateTime(closeFrameTime));
            }

        } catch (Exception e) {
            HandshakeData handshakeData;
            if (Objects.nonNull(wsSession)) {
                handshakeData = wsSession.getHandshakeData();
                listener.onDisconnectException(e, handshakeData);
            } else {
                listener.exceptionCaught(ctx, e);
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        final String channelId = ctx.channel().id().toString();
        debugPrint("exceptionCaught",channelId, cause == null ? "": cause.getClass().getName());
        listener.exceptionCaught(ctx, cause);

        try {
            BaseWsSession wsSession = ChannelWsSessionManager.getSession(channelId);
            if (Objects.nonNull(wsSession)) {
                WsErrorReason reason = new WsErrorReason(CloseCode.UNEXPECTED_CONDITION, cause == null ? "Channel Exception" : cause.getMessage());
                wsSession.onError(reason, cause);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            try {
                ctx.close();
                ctx.disconnect();
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }

    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 虽然是websocket，但在建立websocket连接前，先进行http握手,所以，这时也要处理http请求
        // 在http握手完成后，才是websocket下的通信
        if (msg instanceof FullHttpRequest) {
            handleHttpRequest(ctx, (FullHttpRequest) msg);
        } else if (msg instanceof WebSocketFrame) {
            //websocket接入
            handleWebSocketFrame(ctx, (WebSocketFrame) msg);
        }
    }

    /**
     * 处理websocket消息
     * @param ctx  ChannelHandlerContext
     * @param frame  WebSocketFrame
     */
    private void handleWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) {
        //判断是否是关闭连接的帧
        if (frame instanceof CloseWebSocketFrame) {
            //客户端主动断开连接了
            webSocketServerHandshaker.close(ctx.channel(), (CloseWebSocketFrame)frame.retain());
            onCloseDisconnect(ctx);
        }

        //判断是否是ping/pong
        if (frame instanceof PingWebSocketFrame) {
            ctx.channel().writeAndFlush(new PongWebSocketFrame(frame.content().retain()));
            return;
        } else if (frame instanceof PongWebSocketFrame) {
            ctx.channel().writeAndFlush(new PingWebSocketFrame(frame.content().retain()));
            return;
        }

        String channelId = ctx.channel().id().toString();

        if (frame instanceof TextWebSocketFrame) {
            // 如果是文本帧 业务请求返回消息处理
            String text = ((TextWebSocketFrame) frame).text();
            String remoteAddress = ctx.channel().remoteAddress().toString();
            debugPrint("handleWebSocketFrame", "server 收到 文本消息[" + text + "]", "remoteAddress= "+ remoteAddress);
            //异步处理消息
            ParentExecutorService.getInstance().execute(() -> {
                String responseMsg = null;
                try {
                    responseMsg = (String) ChannelWsSessionManager.getSession(channelId).onMessage(WsMessageType.TEXT, text);
                } catch (Exception e) {
                    log.warn(e.getMessage());
                    ExceptionCollActionEvent event = new ExceptionCollActionEvent(ExceptionType.WEBSOCKET, this.getClass().getName(), e, 5000);
                    SpringContextHolder.publishEvent(event);
                }

                if (StringUtils.isNotBlank(responseMsg)) {
                    //回一个消息
                    ctx.channel().writeAndFlush(new TextWebSocketFrame(responseMsg));
                }
            });
        } else if (frame instanceof BinaryWebSocketFrame) {
            //二进制消息
            ByteBuf byteBuf = frame.content();
            String remoteAddress = ctx.channel().remoteAddress().toString();
            debugPrint("handleWebSocketFrame", "server 收到 二进制消息", "remoteAddress= "+ remoteAddress);
            //异步处理消息
            ParentExecutorService.getInstance().execute(() -> {
                ByteBuf responseMsg = null;
                try {
                    responseMsg = (ByteBuf) ChannelWsSessionManager.getSession(channelId).onMessage(WsMessageType.BINARY, byteBuf);
                } catch (Exception e) {
                    log.warn(e.getMessage());
                    ExceptionCollActionEvent event = new ExceptionCollActionEvent(ExceptionType.WEBSOCKET, this.getClass().getName(), e, 5000);
                    SpringContextHolder.publishEvent(event);
                }

                if (Objects.nonNull(responseMsg)) {
                    //回一个消息
                    ctx.channel().writeAndFlush(new BinaryWebSocketFrame(responseMsg));
                }
            });
        } else {
            UnsupportedOperationException ex = new UnsupportedOperationException(
                    String.format("%s frame types not supported", frame.getClass().getName()));
            WsErrorReason reason = new WsErrorReason(CloseCode.UNEXPECTED_CONDITION,"WS通道收到了不支持的帧数据");
            BaseWsSession session = ChannelWsSessionManager.getSession(channelId);
            if(session != null) {
                session.onError(reason , ex);
            }
        }

    }



    /**
     * 处理http请求
     * @param ctx     ChannelHandlerContext
     * @param request FullHttpRequest
     */
    private void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        // 如果HTTP解码失败，返回HTTP异常
        if (request.decoderResult().isFailure() ||
                (!StringConstants.WEBSOCKET.equals(request.headers().get(StringConstants.Headers.UPGRADE)))) {
            debugPrint("handleHttpRequest", "HTTP解码失败");
            sendHttpResponse(ctx, request, new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
            return;
        }

        //TODO uri安全增强

        //构造握手响应返回
        WebSocketServerHandshakerFactory factory = new WebSocketServerHandshakerFactory("ws://"
                + request.headers().get(HttpHeaderNames.HOST), null, false);
        webSocketServerHandshaker = factory.newHandshaker(request);
        if (Objects.isNull(webSocketServerHandshaker)) {
            //无法处理websocket版本
            debugPrint("webSocketServerHandshaker is null", "无法处理的websocket版本");
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
        } else {
            //向客户端发送websocket握手
            webSocketServerHandshaker.handshake(ctx.channel(), request);
            debugPrint("handshake", "ws version = " + webSocketServerHandshaker.version());
        }

        //业务增强
        String channelId = ctx.channel().id().toString();
        BaseWsSession session = ChannelWsSessionManager.getSession(channelId);
        try {
            if (Objects.isNull(session)) {
                debugPrint("@@@ handleHttpRequest", "SystemError", "not session for channelId = " + channelId);
                throw new RuntimeException("@@@ HandleHttpRequest error, not session for channelId = " + channelId);
            } else {
                //握手数据准备
                boolean result = session.initHandshakeData(request, ctx.channel().remoteAddress());
                log.info("@@@ InitHandshakeData, result:{}, uri:{}", result, request.uri());
                if (result) {
                    try {
                        session.onOpen();
                    } catch (Exception e) {
                        throw new PublishedException(e);
                    }
                } else {
                    //握手失败
                    try {
                        WsErrorReason errorReason = new WsErrorReason(CloseCode.TLS_HANDSHAKE_FAILURE, "handshake data invalidated.");
                        ctx.channel().writeAndFlush(errorReason);
                    } catch (Exception e) {
                        debugPrint("handleHttpRequest", "INNER ERROR", e.getClass().getName(), e.getMessage());
                        debugPrint("handleHttpRequest", "INNER ERROR", "WsErrorReason can not send to client:" + channelId);
                    }
                    //握手无效 服务端关闭所有连接
                    super.channelInactive(ctx);
                    ctx.channel().close();
                    debugPrint("handleHttpRequest", "HandShake ERROR", "握手数据无效，关闭连接:" + channelId);
                }
            }
        } catch (Exception e) {
            listener.exceptionCaught(ctx, e);
            //握手数据无效，服务端关闭连接...
            debugPrint("handleHttpRequest", "Business ERROR", "握手过程出错（业务错误），关闭连接:" + channelId);
            // 握手失败了，检查通道.....
            if (Objects.nonNull(session)) {
                session.onError(new WsErrorReason(CloseCode.TLS_HANDSHAKE_FAILURE, e.getMessage()), e);
            }
            //握手数据无效，服务端关闭所有的连接...
            this.channelInactive(ctx);
        }
    }

    /**
     * 返回httpResponse响应
     *
     * @param ctx      ChannelHandlerContext
     * @param request  FullHttpRequest
     * @param response FullHttpResponse
     */
    private void sendHttpResponse(ChannelHandlerContext ctx, FullHttpRequest request, FullHttpResponse response) {
        int code = response.status().code();
        if (code != HttpResponseStatus.OK.code()) {
            ByteBuf buf = Unpooled.copiedBuffer(response.status().toString(), CharsetUtil.UTF_8);
            response.content().writeBytes(buf);
            buf.release();
            HttpUtil.setContentLength(response, response.content().readableBytes());
        }

        //如果是非keep-alive，关闭连接
        ChannelFuture f = ctx.channel().writeAndFlush(response);
        if (!isKeepAlive(request) || code != HttpResponseStatus.OK.code()) {
            f.addListener(ChannelFutureListener.CLOSE);
        }
    }

    /**
     * isKeepAlive
     * @param req FullHttpRequest
     * @return 判断HTTP请求是否为keep-alive
     */
    private boolean isKeepAlive(FullHttpRequest req) {
        return !req.headers().contains(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE, true)
                && (!req.protocolVersion().equals(HttpVersion.HTTP_1_0)
                || req.headers().contains(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE, true));
    }

    /**
     * 客户端主动断开ws连接
     * @param ctx
     */
    private void onCloseDisconnect(ChannelHandlerContext ctx) {
        String channelId = ctx.channel().id().toString();
        debugPrint("onClientDisconnect","CloseWebSocketFrame", channelId);
        BaseWsSession session = null;
        try {
            session = ChannelWsSessionManager.getSession(channelId);
            closeFrameProcess.put(channelId, new Date());
            session = ChannelWsSessionManager.cancel(channelId);
            if (Objects.nonNull(session)) {
                session.onClose(new WsErrorReason(CloseCode.GOING_AWAY, "onCloseDisconnect"), CloseScene.SCENE_CLIENT_END_DISCONNECT);
            }
        } catch (Exception e) {
            log.warn(e.getClass().getName()  + " | "+ e.getMessage());
            HandshakeData handshakeData;
            if (Objects.nonNull(session)) {
                handshakeData = session.getHandshakeData();
                listener.onDisconnectException(e, handshakeData);
            } else {
                try {
                    listener.exceptionCaught(ctx, e);
                } catch (Exception ex) {
                    log.warn(e.getClass().getName()  + " | "+ e.getMessage());
                }
            }
        } finally {
            Channel channel = ctx.channel();
            boolean result = Objects.nonNull(channel) && (channel.isActive() || channel.isOpen());
            if (result) {
                ctx.close();
                ctx.disconnect();
            }
        }

    }

}
