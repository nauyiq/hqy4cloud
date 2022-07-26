/*
 * Copyright (C) 2012-2013 Facebook, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.facebook.nifty.client;

import com.facebook.nifty.core.TChannelBufferInputTransport;
import com.facebook.nifty.duplex.TDuplexProtocolFactory;
import com.hqy.base.common.swticher.CommonSwitcher;
import io.airlift.units.Duration;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TMessage;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.*;
import org.jboss.netty.channel.socket.nio.NioSocketChannel;
import org.jboss.netty.handler.timeout.ReadTimeoutException;
import org.jboss.netty.handler.timeout.WriteTimeoutException;
import org.jboss.netty.util.Timeout;
import org.jboss.netty.util.Timer;
import org.jboss.netty.util.TimerTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@NotThreadSafe
public abstract class AbstractClientChannel extends SimpleChannelHandler implements
        NiftyClientChannel {
    private static final Logger log = LoggerFactory.getLogger(AbstractClientChannel.class);

    private final Channel nettyChannel;
    private Duration sendTimeout = null;

    // Timeout until the whole request must be received.
    private Duration receiveTimeout = null;

    // Timeout for not receiving any data from the server
    private Duration readTimeout = null;

    /**
     * 通过当前channel发送的请求的集合
     * K： 请求序列id sequenceId， V: Request
     */
    private final Map<Integer, Request> requestMap = new HashMap<>();
    private volatile TException channelError;
    private final Timer timer;
    private final TDuplexProtocolFactory protocolFactory;

    protected AbstractClientChannel(Channel nettyChannel, Timer timer, TDuplexProtocolFactory protocolFactory) {
        this.nettyChannel = nettyChannel;
        this.timer = timer;
        this.protocolFactory = protocolFactory;
    }

    @Override
    public Channel getNettyChannel() {
        return nettyChannel;
    }

    @Override
    public TDuplexProtocolFactory getProtocolFactory() {
        return protocolFactory;
    }

    protected abstract ChannelBuffer extractResponse(Object message) throws TTransportException;


    protected int extractSequenceId(ChannelBuffer messageBuffer)
            throws TTransportException {
        try {
            messageBuffer.markReaderIndex();
            TTransport inputTransport = new TChannelBufferInputTransport(messageBuffer);
            TProtocol inputProtocol = getProtocolFactory().getInputProtocolFactory().getProtocol(inputTransport);
            TMessage message = inputProtocol.readMessageBegin();
            messageBuffer.resetReaderIndex();
            return message.seqid;
        } catch (Throwable t) {
            throw new TTransportException("Could not find sequenceId in Thrift message");
        }
    }

    protected TMessage extractTMessage(ChannelBuffer messageBuffer)
            throws TTransportException {
        try {
            messageBuffer.markReaderIndex();
            TTransport inputTransport = new TChannelBufferInputTransport(messageBuffer);
            TProtocol inputProtocol = getProtocolFactory().getInputProtocolFactory().getProtocol(inputTransport);
            TMessage message = inputProtocol.readMessageBegin();
            messageBuffer.resetReaderIndex();
            return message;
        } catch (Throwable t) {
            throw new TTransportException("Could not find sequenceId in Thrift message");
        }
    }

    protected abstract ChannelFuture writeRequest(ChannelBuffer request);

    @Override
    public void close() {
        try {
            log.info("Prevent Memory Leak.");
            cancelAllRequestTimeouts();
        } catch (Exception e) {
            log.error("Failed execute to cancel all timeout jab,  cause {}", e.getMessage(), e);
        }
        getNettyChannel().close();
    }

    @Override
    public void setSendTimeout(@Nullable Duration sendTimeout) {
        this.sendTimeout = sendTimeout;
    }

    @Override
    public Duration getSendTimeout() {
        return sendTimeout;
    }

    @Override
    public void setReceiveTimeout(@Nullable Duration receiveTimeout) {
        this.receiveTimeout = receiveTimeout;
    }

    @Override
    public Duration getReceiveTimeout() {
        return receiveTimeout;
    }

    @Override
    public void setReadTimeout(@Nullable Duration readTimeout) {
        this.readTimeout = readTimeout;
    }

    @Override
    public Duration getReadTimeout() {
        return this.readTimeout;
    }

    @Override
    public boolean hasError() {
        return channelError != null;
    }

    @Override
    public TException getError() {
        return channelError;
    }

    @Override
    public void executeInIoThread(Runnable runnable) {
        NioSocketChannel nioSocketChannel = (NioSocketChannel) getNettyChannel();
        nioSocketChannel.getWorker().executeInIoThread(runnable, true);
    }

    /**
     * 通过当前通达 发送rpc消息 （发起rpc请求...）
     *
     * @see RequestChannel#sendAsynchronousRequest(ChannelBuffer, boolean, Listener)
     */
    @Override
    public void sendAsynchronousRequest(final ChannelBuffer message,
                                        final boolean oneway,
                                        final Listener listener)
            throws TException {
        //final int sequenceId = extractSequenceId(message);
        //获取sequenceId之外 更多处理请求相关的上下文信息
        final TMessage tmsg = extractTMessage(message);
        final int sequenceId = tmsg.seqid;
        final String name = tmsg.name;
        if (CommonSwitcher.JUST_4_TEST_DEBUG.isOn()) {
            log.info("sendAsynchronousRequest >> name={},sequenceId={}, {}", name, sequenceId, requestMap.size());
        }

        // Ensure channel listeners are always called on the channel's I/O thread
        executeInIoThread(new Runnable() {
            @Override
            public void run() {
                try {
                    final Request request = makeRequest(sequenceId, listener);
                    //增加一个TMessage的name 属性，关联记录当前rpc消息的名称..
                    request.setTmessageName(name);
                    if (!nettyChannel.isConnected()) {
                        fireChannelErrorCallback(listener, new TTransportException(TTransportException.NOT_OPEN, "Channel closed"));
                        return;
                    }

                    if (hasError()) {
                        fireChannelErrorCallback(
                                listener,
                                new TTransportException(TTransportException.UNKNOWN, "Channel is in a bad state due to failing a previous request"));
                        return;
                    }

                    ChannelFuture sendFuture = writeRequest(message);

//                    queueSendTimeout(request, oneway, sequenceId);
                    queueSendTimeout(request);

                    sendFuture.addListener(new ChannelFutureListener() {
                        @Override
                        public void operationComplete(ChannelFuture future) throws Exception {
                            messageSent(future, request, oneway);
                            //场景：内存泄漏  (同步和异步都会进入此 【留意】    sendAsynchronousRequest
                            //只有异步 oneway的方式需要进行如下操作。同步的方式会在接收处移除此，故同步 的没有泄漏。
                            handlerOnewayMemoryLeak(oneway, sequenceId);
                        }


                    });
                } catch (Throwable t) {
                    // onError calls all registered listeners in the requestMap, but this request
                    // may not be registered yet. So we try to remove it (to make sure we don't call
                    // the callback twice) and then manually make the callback for this request
                    // listener.
                    requestMap.remove(sequenceId);
                    fireChannelErrorCallback(listener, t);

                    onError(t);
                }
            }
        });
    }


    /**
     * 场景：内存泄漏  (同步和异步都会进入此 【留意】    sendAsynchronousRequest
     * 只有异步 oneway的方式需要进行如下操作。同步的方式会在接收处移除此，故同步 的没有泄漏。
     * @param oneway
     * @param sequenceId
     */
    private void handlerOnewayMemoryLeak(final boolean oneway, final int sequenceId) {
        if (oneway && CommonSwitcher.ENABLE_RPC_CLIENT_CHANNEL_LEAK_PROTECTION.isOn()) {
            requestMap.remove(sequenceId);
        }
    }

    private void messageSent(ChannelFuture future, Request request, boolean oneway) {

        try {
            if (future.isSuccess()) {
                cancelRequestTimeouts(request);
                fireRequestSentCallback(request.getListener());
                if (oneway) {
                    retireRequest(request);
                } else {
                    queueReceiveAndReadTimeout(request);
                }
            } else {
                //手动cancel掉相关定时任务
                cancelRequestTimeouts(request);
                TTransportException transportException =
                        new TTransportException("Sending request failed",
                                future.getCause());
                onError(transportException);
            }
        } catch (Throwable t) {
            onError(t);
        }
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
        try {
            ChannelBuffer response = extractResponse(e.getMessage());

            if (response != null) {
                int sequenceId = extractSequenceId(response);
                //优化by rosun 获取sequenceId之外 更多处理请求相关的上下文信息
//                final TMessage tmsg  =  extractTMessage(response);
//                final int sequenceId = tmsg.seqid;
                onResponseReceived(sequenceId, response);
            } else {
                ctx.sendUpstream(e);
            }
        } catch (Throwable t) {
            onError(t);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent event)
            throws Exception {
        Throwable t = event.getCause();
        onError(t);
    }

    private Request makeRequest(int sequenceId, Listener listener) {
        Request request = new Request(listener);
        requestMap.put(sequenceId, request);
        return request;
    }

    private void retireRequest(Request request) {
        cancelRequestTimeouts(request);
    }


    private void cancelRequestTimeouts(Request request) {
        try {
            Timeout sendTimeout = request.getSendTimeout();
            if (sendTimeout != null && !sendTimeout.isCancelled()) {
                sendTimeout.cancel();
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        try {
            Timeout receiveTimeout = request.getReceiveTimeout();
            if (receiveTimeout != null && !receiveTimeout.isCancelled()) {
                receiveTimeout.cancel();
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        try {
            Timeout readTimeout = request.getReadTimeout();
            if (readTimeout != null && !readTimeout.isCancelled()) {
                readTimeout.cancel();
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

    }

    private void cancelAllRequestTimeouts() {
        for (Request request : requestMap.values()) {
            cancelRequestTimeouts(request);
        }
    }

    private void onResponseReceived(int sequenceId, ChannelBuffer response) {
        Request request = requestMap.remove(sequenceId);
        if (request == null) {
            onError(new TTransportException("Bad sequence id in response: " + sequenceId));
        } else {
            retireRequest(request);
            fireResponseReceivedCallback(request.getListener(), response);
        }
    }

    @Override
    public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        if (!requestMap.isEmpty()) {
            onError(new TTransportException("Client was disconnected by server"));
        }
    }

    protected void onError(Throwable t) {
        log.warn("onError:1 出错了~ " + t.getMessage(), t);
        TException wrappedException = wrapException(t);

        if (channelError == null) {
            channelError = wrappedException;
        }

        log.warn("onError:2 fireChannelErrorCallback;");
        Collection<Request> requests = new ArrayList<>();
        requests.addAll(requestMap.values());
        for (Request request : requests) {
            try {
                fireChannelErrorCallback(request.getListener(), wrappedException);
            } catch (Exception e) {
                log.warn(e.getMessage() + " | " + e.getClass().getName());
            }
        }
        /*** 聚合到disposeMemoryLeak() 方法
         cancelAllTimeouts();
         Channel channel = getNettyChannel();
         if (nettyChannel.isOpen())
         {
         LOGGER.warn("onError:3 channel.close();关闭当前channel;");
         channel.close();
         }
         ***/
        //聚合到disposeMemoryLeak() 方法
        disposeMemoryLeak();


    }

    /**
     * 2020 1230 防止内存泄露,channel有问题的时候 清理掉 残留的东东
     */
    public void disposeMemoryLeak() {
        try {
            log.warn("onError:3 cancelAllTimeouts; 取消所有的超时定时任务;");
            cancelAllRequestTimeouts();
            //cancel完定时任务后，务必手动清理掉MAP 防止内存泄露...
            requestMap.clear();
            log.warn("onError:4 channel.close(); 清空 requestMap;");
            Channel channel = getNettyChannel();
            if (nettyChannel.isOpen()) {
                log.warn("onError:5 channel.close();关闭当前channel;");
                channel.close();
            }
        } catch (Exception e) {
            log.warn(e.getMessage() + " | " + e.getClass().getName());
        }
    }

    protected TException wrapException(Throwable t) {
        if (t instanceof TException) {
            return (TException) t;
        } else {
            return new TTransportException(t);
        }
    }

    private void fireRequestSentCallback(Listener listener) {
        try {
            listener.onRequestSent();
        } catch (Throwable t) {
            log.warn("Request sent listener callback triggered an exception: {}", t);
        }
    }

    private void fireResponseReceivedCallback(Listener listener, ChannelBuffer response) {
        try {
            listener.onResponseReceived(response);
        } catch (Throwable t) {
            log.warn("Response received listener callback triggered an exception: {}", t);
        }
    }

    private void fireChannelErrorCallback(Listener listener, TException exception) {
        try {
            listener.onChannelError(exception);
        } catch (Throwable t) {
            log.warn("Channel error listener callback triggered an exception: {}", t);
        }
    }

    private void fireChannelErrorCallback(Listener listener, Throwable throwable) {
        fireChannelErrorCallback(listener, wrapException(throwable));
    }

    private void onSendTimeoutFired(Request request) {
        //modify by luoshan start 添加钉钉告警
        log.warn("警告，IGNORED RPC TIME OUT（onSendTimeoutFired） ,兼容 超时模式.RPC调用已超时！本次超时被忽略，可以继续调用。");
//        DingdingWarnService.warn("onSendTimeoutFired" ,getRemoteAddr(),new String[] {request.getTmessageName()} );
        cancelAllRequestTimeouts();
        WriteTimeoutException timeoutException = new WriteTimeoutException("Timed out waiting " + getSendTimeout() + " to send data to server");
        fireChannelErrorCallback(request.getListener(), new TTransportException(TTransportException.TIMED_OUT, timeoutException));
    }

    private String getRemoteAddr() {
        String remoteString = null;
        try {
            remoteString = this.nettyChannel.getRemoteAddress().toString();
        } catch (Exception e) {
            remoteString = "";
        }
        return remoteString;
    }

    private void onReceiveTimeoutFired(Request request) {
        //modify by luoshan start 添加钉钉告警
        log.warn("警告，IGNORED RPC TIME OUT（onReceiveTimeoutFired） ,兼容 超时模式.RPC调用已超时！本次超时被忽略，可以继续调用。");
//        DingdingWarnService.warn("onReceiveTimeoutFired", getRemoteAddr(),new String[] {request.getTmessageName()} );
        cancelAllRequestTimeouts();
        ReadTimeoutException timeoutException = new ReadTimeoutException("Timed out waiting " + getReceiveTimeout() + " to receive response");
        fireChannelErrorCallback(request.getListener(), new TTransportException(TTransportException.TIMED_OUT, timeoutException));
    }

    private void onReadTimeoutFired(Request request) {
        //modify by luoshan start 添加钉钉告警
//        DingdingWarnService.warn("onReadTimeoutFired",getRemoteAddr(),new String[] {request.getTmessageName()});
        log.warn("警告，IGNORED RPC TIME OUT（onReadTimeoutFired） ,兼容 超时模式.RPC调用已超时！本次超时被忽略，可以继续调用。");
        cancelAllRequestTimeouts();
        ReadTimeoutException timeoutException = new ReadTimeoutException("Timed out waiting " + getReadTimeout() + " to read data from server");
        fireChannelErrorCallback(request.getListener(), new TTransportException(TTransportException.TIMED_OUT, timeoutException));
    }


    private void queueSendTimeout(final Request request) throws TTransportException {
        if (this.sendTimeout != null) {
            long sendTimeoutMs = this.sendTimeout.toMillis();
            if (sendTimeoutMs > 0) {
                TimerTask sendTimeoutTask = new IoThreadBoundTimerTask(this, new TimerTask() {
                    @Override
                    public void run(Timeout timeout) {
                        onSendTimeoutFired(request);
                    }
                });

                Timeout sendTimeout;
                try {
                    sendTimeout = timer.newTimeout(sendTimeoutTask, sendTimeoutMs, TimeUnit.MILLISECONDS);
                } catch (IllegalStateException e) {
                    throw new TTransportException("Unable to schedule send timeout");
                }
                request.setSendTimeout(sendTimeout);
            }
        }
    }

    private void queueReceiveAndReadTimeout(final Request request) throws TTransportException {
        if (this.receiveTimeout != null) {
            long receiveTimeoutMs = this.receiveTimeout.toMillis();
            if (receiveTimeoutMs > 0) {
                TimerTask receiveTimeoutTask = new IoThreadBoundTimerTask(this, new TimerTask() {
                    @Override
                    public void run(Timeout timeout) {
                        onReceiveTimeoutFired(request);
                    }
                });

                Timeout timeout;
                try {
                    timeout = timer.newTimeout(receiveTimeoutTask, receiveTimeoutMs, TimeUnit.MILLISECONDS);
                } catch (IllegalStateException e) {
                    throw new TTransportException("Unable to schedule request timeout");
                }
                request.setReceiveTimeout(timeout);
            }
        }

        if (this.readTimeout != null) {
            long readTimeoutNanos = this.readTimeout.roundTo(TimeUnit.NANOSECONDS);
            if (readTimeoutNanos > 0) {
                TimerTask readTimeoutTask = new IoThreadBoundTimerTask(this, new ReadTimeoutTask(readTimeoutNanos, request));

                Timeout timeout;
                try {
                    timeout = timer.newTimeout(readTimeoutTask, readTimeoutNanos, TimeUnit.NANOSECONDS);
                } catch (IllegalStateException e) {
                    throw new TTransportException("Unable to schedule read timeout");
                }
                request.setReadTimeout(timeout);
            }
        }
    }


    /**
     * Used to create TimerTasks that will fire
     */
    private static class IoThreadBoundTimerTask implements TimerTask {
        private final NiftyClientChannel channel;
        private final TimerTask timerTask;

        public IoThreadBoundTimerTask(NiftyClientChannel channel, TimerTask timerTask) {
            this.channel = channel;
            this.timerTask = timerTask;
        }

        @Override
        public void run(final Timeout timeout)
                throws Exception {
            channel.executeInIoThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        timerTask.run(timeout);
                    } catch (Exception e) {
                        Channels.fireExceptionCaught(channel.getNettyChannel(), e);
                    }
                }
            });
        }
    }

    /**
     * Bundles the details of a client request that has started, but for which a response hasn't
     * yet been received (or in the one-way case, the send operation hasn't completed yet).
     */
    private static class Request {
        private final Listener listener;
        private Timeout sendTimeout;
        private Timeout receiveTimeout;
        private String tmessageName;

        private volatile Timeout readTimeout;

        public Request(Listener listener) {
            this.listener = listener;
        }

        public Listener getListener() {
            return listener;
        }

        public Timeout getReceiveTimeout() {
            return receiveTimeout;
        }

        public void setReceiveTimeout(Timeout receiveTimeout) {
            this.receiveTimeout = receiveTimeout;
        }

        public Timeout getReadTimeout() {
            return readTimeout;
        }

        public void setReadTimeout(Timeout readTimeout) {
            this.readTimeout = readTimeout;
        }

        public Timeout getSendTimeout() {
            return sendTimeout;
        }

        public void setSendTimeout(Timeout sendTimeout) {
            this.sendTimeout = sendTimeout;
        }

        public String getTmessageName() {

            return tmessageName;
        }

        public void setTmessageName(String tmessageName) {

            this.tmessageName = tmessageName;
        }
    }

    private final class ReadTimeoutTask implements TimerTask {
        private final TimeoutHandler timeoutHandler;
        private final long timeoutNanos;
        private final Request request;

        ReadTimeoutTask(long timeoutNanos, Request request) {
            this.timeoutHandler = TimeoutHandler.findTimeoutHandler(getNettyChannel().getPipeline());
            this.timeoutNanos = timeoutNanos;
            this.request = request;
        }

        @Override
        public void run(Timeout timeout) throws Exception {
            if (timeoutHandler == null) {
                return;
            }

            if (timeout.isCancelled()) {
                return;
            }

            if (!getNettyChannel().isOpen()) {
                return;
            }

            long currentTimeNanos = System.nanoTime();

            long timePassed = currentTimeNanos - timeoutHandler.getLastMessageReceivedNanos();
            long nextDelayNanos = timeoutNanos - timePassed;

            if (nextDelayNanos <= 0) {
                onReadTimeoutFired(request);
            } else {
                request.setReadTimeout(timer.newTimeout(this, nextDelayNanos, TimeUnit.NANOSECONDS));
            }
        }
    }
}
