/*
 * Copyright (C) 2012 Facebook, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.facebook.swift.service;

import com.facebook.nifty.client.RequestChannel;
import com.facebook.nifty.core.RequestContext;
import com.facebook.nifty.core.RequestContexts;
import com.facebook.nifty.core.TChannelBufferInputTransport;
import com.facebook.nifty.core.TChannelBufferOutputTransport;
import com.facebook.swift.codec.ThriftCodec;
import com.facebook.swift.codec.ThriftCodecManager;
import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.codec.ThriftIdlAnnotation;
import com.facebook.swift.codec.internal.TProtocolReader;
import com.facebook.swift.codec.internal.TProtocolWriter;
import com.facebook.swift.codec.metadata.*;
import com.facebook.swift.service.metadata.ThriftMethodMetadata;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.util.concurrent.AbstractFuture;
import com.google.common.util.concurrent.ListenableFuture;
import com.hqy.foundation.common.thread.ProjectThreadLocalContext;
import com.hqy.rpc.thrift.RemoteExParam;
import com.hqy.util.ArgsUtil;
import com.hqy.util.identity.ProjectSnowflakeIdWorker;
import org.apache.thrift.TApplicationException;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TMessage;
import org.apache.thrift.protocol.TProtocol;
import org.jboss.netty.buffer.ChannelBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.weakref.jmx.Managed;

import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.facebook.swift.codec.metadata.FieldKind.THRIFT_FIELD;
import static org.apache.thrift.TApplicationException.*;
import static org.apache.thrift.protocol.TMessageType.*;

@ThreadSafe
public class ThriftMethodHandler {

    private static final Logger log = LoggerFactory.getLogger(ThriftMethodHandler.class);

    private final String name;
    private final String qualifiedName;
    private final List<ParameterHandler> parameterCodecs;
    private final ThriftCodec<Object> successCodec;
    private final Map<Short, ThriftCodec<Object>> exceptionCodecs;
    private final boolean oneway;

    private final boolean invokeAsynchronously;

    public ThriftMethodHandler(ThriftMethodMetadata methodMetadata, ThriftCodecManager codecManager) {
        name = methodMetadata.getName();
        qualifiedName = methodMetadata.getQualifiedName();
        invokeAsynchronously = methodMetadata.isAsync();
        oneway = methodMetadata.getOneway();


        //业务调用链中的方法参数.
        List<ThriftFieldMetadata> originFieldMetadataList = methodMetadata.getParameters();
        //修改源码 动态植入拓展参数.
        ThriftFieldMetadata injectGeneralParamFieldMetadata = injectGeneralParamFieldMetadata(originFieldMetadataList, codecManager);
        originFieldMetadataList.add(injectGeneralParamFieldMetadata);

        // get the thrift codecs for the parameters
        ParameterHandler[] parameters = new ParameterHandler[originFieldMetadataList.size()];
        for (ThriftFieldMetadata fieldMetadata : originFieldMetadataList) {
            ThriftParameterInjection parameter = (ThriftParameterInjection) fieldMetadata.getInjections().get(0);

            ParameterHandler handler = new ParameterHandler(
                    fieldMetadata.getId(),
                    fieldMetadata.getName(),
                    (ThriftCodec<Object>) codecManager.getCodec(fieldMetadata.getThriftType()));

            parameters[parameter.getParameterIndex()] = handler;
        }
        parameterCodecs = ImmutableList.copyOf(parameters);

        // get the thrift codecs for the exceptions
        ImmutableMap.Builder<Short, ThriftCodec<Object>> exceptions = ImmutableMap.builder();
        for (Map.Entry<Short, ThriftType> entry : methodMetadata.getExceptions().entrySet()) {
            exceptions.put(entry.getKey(), (ThriftCodec<Object>) codecManager.getCodec(entry.getValue()));
        }
        exceptionCodecs = exceptions.build();

        // get the thrift codec for the return value
        successCodec = (ThriftCodec<Object>) codecManager.getCodec(methodMetadata.getReturnType());
    }


    public static ThriftFieldMetadata injectGeneralParamFieldMetadata(List<ThriftFieldMetadata> originFieldMetadataList, ThriftCodecManager codecManager) {
        //拼接到业务参数的默认 因此长度+1
        short parameterId = (short) (originFieldMetadataList.size() + 1);
        String parameterName = RemoteExParam.class.getSimpleName();
        Type parameterType = RemoteExParam.class;
        //构建thrift注入对象 用于参数注入
        ThriftInjection parameterInjection = new ThriftParameterInjection(parameterId, parameterName, originFieldMetadataList.size(), parameterType);
        ThriftType thriftType = codecManager.getCatalog().getThriftType(parameterType);

        Field[] fields = RemoteExParam.class.getDeclaredFields();
        Map<String, String> parameterIdlAnnotations = new HashMap<>(fields.length);
        ImmutableMap.Builder<String, String> idlAnnotationsBuilder = ImmutableMap.builder();
        boolean isLegacyId = false;
        for (Field field : fields) {
            ThriftField thriftField = null;
            for (Annotation annotation : field.getAnnotations()) {
                if (annotation instanceof ThriftField) {
                    thriftField = (ThriftField) annotation;
                    break;
                }
            }
            //不可能为null 因为GeneralParam是硬编码的类.
            if (thriftField == null) {
                break;
            }
            isLegacyId = thriftField.isLegacyId();
            for (ThriftIdlAnnotation idlAnnotation : thriftField.idlAnnotations()) {
                idlAnnotationsBuilder.put(idlAnnotation.key(), idlAnnotation.value());
            }
            parameterIdlAnnotations = idlAnnotationsBuilder.build();
        }

        return new ThriftFieldMetadata(parameterId, isLegacyId, false,
                ThriftField.Requiredness.NONE,
                parameterIdlAnnotations,
                new DefaultThriftTypeReference(thriftType),
                parameterName, THRIFT_FIELD,
                ImmutableList.of(parameterInjection),
                Optional.absent(),
                Optional.absent(),
                Optional.absent(),
                Optional.absent());
    }



    @Managed
    public String getName() {
        return name;
    }

    public String getQualifiedName() {
        return qualifiedName;
    }

    public Object invoke(
            final RequestChannel channel,
            final TChannelBufferInputTransport inputTransport,
            final TChannelBufferOutputTransport outputTransport,
            final TProtocol inputProtocol,
            final TProtocol outputProtocol,
            final int sequenceId,
            final ClientContextChain contextChain,
            final Object... args)
            throws Exception {
        if (invokeAsynchronously) {
            // This method declares a Future return value: run it asynchronously
            return asynchronousInvoke(channel, inputTransport, outputTransport, inputProtocol, outputProtocol, sequenceId, contextChain, args);
        } else {
            try {
                // This method declares an immediate return value: run it synchronously
                return synchronousInvoke(channel, inputTransport, outputTransport, inputProtocol, outputProtocol, sequenceId, contextChain, args);
            } finally {
                contextChain.done();
            }
        }
    }

    private Object synchronousInvoke(
            RequestChannel channel,
            TChannelBufferInputTransport inputTransport,
            TChannelBufferOutputTransport outputTransport,
            TProtocol inputProtocol,
            TProtocol outputProtocol,
            int sequenceId,
            ClientContextChain contextChain,
            Object[] args)
            throws Exception {
        Object results = null;

        try {
            //在源码基础 业务无感知的动态拼接一个参数.
            args = addDynamicArgs(args);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        // write request
        contextChain.preWrite(args);
        outputTransport.resetOutputBuffer();
        writeArguments(outputProtocol, sequenceId, args);
        // Don't need to copy the output buffer for sync case
        ChannelBuffer requestBuffer = outputTransport.getOutputBuffer();
        contextChain.postWrite(args);

        if (!this.oneway) {
            ChannelBuffer responseBuffer;

            try {
                responseBuffer = SyncClientHelpers.sendSynchronousTwoWayMessage(channel, requestBuffer);
            } catch (Exception e) {
                contextChain.preReadException(e);
                throw e;
            }

            // read results
            contextChain.preRead();
            try {
                inputTransport.setInputBuffer(responseBuffer);
                waitForResponse(inputProtocol, sequenceId);
                results = readResponse(inputProtocol);
                contextChain.postRead(results);
            } catch (Exception e) {
                contextChain.postReadException(e);
                throw e;
            }
        } else {
            try {
                SyncClientHelpers.sendSynchronousOneWayMessage(channel, requestBuffer);
            } catch (Exception e) {
                throw e;
            }
        }

        return results;
    }



    public ListenableFuture<Object> asynchronousInvoke(
            final RequestChannel channel,
            final TChannelBufferInputTransport inputTransport,
            final TChannelBufferOutputTransport outputTransport,
            final TProtocol inputProtocol,
            final TProtocol outputProtocol,
            final int sequenceId,
            final ClientContextChain contextChain,
                  Object[] args)
            throws Exception {
        final AsyncMethodCallFuture<Object> future = AsyncMethodCallFuture.create(contextChain);
        final RequestContext requestContext = RequestContexts.getCurrentContext();

        try {
            args = addDynamicArgs(args);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        contextChain.preWrite(args);
        outputTransport.resetOutputBuffer();
        writeArguments(outputProtocol, sequenceId, args);
        ChannelBuffer requestBuffer = outputTransport.getOutputBuffer().copy();
        contextChain.postWrite(args);

        // send message and setup listener to handle the response
        channel.sendAsynchronousRequest(requestBuffer, false, new RequestChannel.Listener() {
            @Override
            public void onRequestSent() {
                if (oneway) {
                    try {
                        future.set(null);
                    } catch (Exception e) {
                        future.setException(e);
                    }
                }
            }

            @Override
            public void onResponseReceived(ChannelBuffer message) {
                RequestContext oldRequestContext = RequestContexts.getCurrentContext();
                RequestContexts.setCurrentContext(requestContext);
                try {
                    contextChain.preRead();
                    inputTransport.setInputBuffer(message);
                    waitForResponse(inputProtocol, sequenceId);
                    Object results = readResponse(inputProtocol);
                    contextChain.postRead(results);
                    future.set(results);
                } catch (Exception e) {
                    contextChain.postReadException(e);
                    future.setException(e);
                } finally {
                    RequestContexts.setCurrentContext(oldRequestContext);
                }
            }

            @Override
            public void onChannelError(TException e) {
                RequestContext oldRequestContext = RequestContexts.getCurrentContext();
                RequestContexts.setCurrentContext(requestContext);
                try {
                    contextChain.preReadException(e);
                    future.setException(e);
                } finally {
                    RequestContexts.setCurrentContext(oldRequestContext);
                }
            }
        });

        return future;
    }

    private Object readResponse(TProtocol in)
            throws Exception {
        TProtocolReader reader = new TProtocolReader(in);
        reader.readStructBegin();
        Object results = null;
        Exception exception = null;
        while (reader.nextField()) {
            if (reader.getFieldId() == 0) {
                results = reader.readField(successCodec);
            } else {
                ThriftCodec<Object> exceptionCodec = exceptionCodecs.get(reader.getFieldId());
                if (exceptionCodec != null) {
                    exception = (Exception) reader.readField(exceptionCodec);
                } else {
                    reader.skipFieldData();
                }
            }
        }
        reader.readStructEnd();
        in.readMessageEnd();

        if (exception != null) {
            throw exception;
        }

        if (successCodec.getType() == ThriftType.VOID) {
            // TODO: check for non-null return from a void function?
            return null;
        }

        if (results == null) {
            throw new TApplicationException(TApplicationException.MISSING_RESULT, name + " failed: unknown result");
        }
        return results;
    }

    private void writeArguments(TProtocol out, int sequenceId, Object[] args)
            throws Exception {
        // Note that though setting message type to ONEWAY can be helpful when looking at packet
        // captures, some clients always send CALL and so servers are forced to rely on the "oneway"
        // attribute on thrift method in the interface definition, rather than checking the message
        // type.
        out.writeMessageBegin(new TMessage(name, oneway ? ONEWAY : CALL, sequenceId));

        // write the parameters
        TProtocolWriter writer = new TProtocolWriter(out);
        writer.writeStructBegin(name + "_args");
        for (int i = 0; i < args.length; i++) {
            Object value = args[i];
            ParameterHandler parameter = parameterCodecs.get(i);
            writer.writeField(parameter.getName(), parameter.getId(), parameter.getCodec(), value);
        }
        writer.writeStructEnd();

        out.writeMessageEnd();
        out.getTransport().flush();
    }

    private void waitForResponse(TProtocol in, int sequenceId)
            throws TException {
        TMessage message = in.readMessageBegin();
        if (message.type == EXCEPTION) {
            TApplicationException exception = TApplicationException.read(in);
            in.readMessageEnd();
            throw exception;
        }
        if (message.type != REPLY) {
            throw new TApplicationException(INVALID_MESSAGE_TYPE,
                    "Received invalid message type " + message.type + " from server");
        }
        if (!message.name.equals(this.name)) {
            throw new TApplicationException(WRONG_METHOD_NAME,
                    "Wrong method name in reply: expected " + this.name + " but received " + message.name);
        }
        if (message.seqid != sequenceId) {
            throw new TApplicationException(BAD_SEQUENCE_ID, name + " failed: out of sequence response");
        }
    }

    private Object[] addDynamicArgs(Object[] args) {
        Long rootId = ProjectThreadLocalContext.ROOT_ID.get();
        if (rootId == null) {
            rootId = 0L;
        }
        Long parentId = ProjectThreadLocalContext.PARENT_ID.get();
        if (parentId == null) {
            parentId = 0L;
        }
        long childId = ProjectSnowflakeIdWorker.getInstance().nextId();
        RemoteExParam param = new RemoteExParam(rootId.toString(), parentId.toString(), childId + "", oneway);
        args = ArgsUtil.addArg(args, param);
        return args;
    }

    private static final class ParameterHandler {
        private final short id;
        private final String name;
        private final ThriftCodec<Object> codec;

        private ParameterHandler(short id, String name, ThriftCodec<Object> codec) {
            this.id = id;
            this.name = name;
            this.codec = codec;
        }

        public short getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public ThriftCodec<Object> getCodec() {
            return codec;
        }
    }

    private static final class AsyncMethodCallFuture<T> extends AbstractFuture<T> {
        private final ClientContextChain contextChain;

        public static <T> AsyncMethodCallFuture<T> create(ClientContextChain contextChain) {
            return new AsyncMethodCallFuture<>(contextChain);
        }

        private AsyncMethodCallFuture(ClientContextChain contextChain) {
            this.contextChain = contextChain;
        }

        @Override
        public boolean set(@Nullable T value) {
            contextChain.done();
            return super.set(value);
        }

        @Override
        public boolean setException(Throwable throwable) {
            contextChain.done();
            return super.setException(throwable);
        }

        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {
            // Async call futures represent requests running on some other service,
            // there is no way to cancel the request once it has been sent.
            return false;
        }
    }
}
