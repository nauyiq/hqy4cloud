package com.hqy.netty.websocket.handler;

import cn.hutool.core.date.DateUtil;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.hqy.fundation.common.base.lang.BaseStringConstants;
import com.hqy.fundation.common.swticher.CommonSwitcher;
import com.hqy.netty.websocket.base.HandshakeData;
import com.hqy.netty.websocket.base.WsErrorReason;
import com.hqy.netty.websocket.base.enums.CloseCode;
import com.hqy.netty.websocket.base.enums.CloseScene;
import com.hqy.netty.websocket.handler.bind.DefaultExceptionListener;
import com.hqy.netty.websocket.handler.bind.ExceptionListener;
import com.hqy.netty.websocket.session.BaseWsSession;
import com.hqy.netty.websocket.session.ChannelWsSessionManager;
import com.hqy.util.JsonUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * websocket处理的pipeline handler 基于RFC 6455文档开发;
 * https://datatracker.ietf.org/doc/rfc6455/
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/3/8 15:30
 */
public class WebsocketHandler extends SimpleChannelInboundHandler<Object> {

    private static final Logger log = LoggerFactory.getLogger(WebsocketHandler.class);

    private final Class<? extends BaseWsSession> wsSessionClass;

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
     * 处理http请求
     * @param ctx ChannelHandlerContext
     * @param request FullHttpRequest
     */
    private void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest request) {
        // 如果HTTP解码失败，返回HTTP异常
        if (request.decoderResult().isFailure() ||
                (!BaseStringConstants.WEBSOCKET.equals(request.headers().get(BaseStringConstants.UPGRADE)))) {
            debugPrint("handleHttpRequest", "HTTP解码失败");
        }

    }

    /**
     * 返回httpResponse响应
     * @param ctx ChannelHandlerContext
     * @param request FullHttpRequest
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
    }

}
