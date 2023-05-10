package com.facebook.swift.service.exception.support;

import cn.hutool.core.map.MapUtil;
import com.facebook.swift.service.exception.ThriftCustomExceptionFactory;
import com.facebook.swift.service.exception.ThriftExceptionInformation;
import org.apache.thrift.TApplicationException;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TMessage;
import org.apache.thrift.protocol.TMessageType;
import org.apache.thrift.protocol.TProtocol;

import java.util.Map;
import java.util.Objects;
import java.util.ServiceLoader;

/**
 * thrift生产者端异常注册中心.
 * 即缓存了系统兼容的异常类型，在检查到异常时服务端定义的异常时则不再抛出TApplicationException
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/5/9 9:22
 */
public class ThriftServerExceptionRegistry {

    private ThriftServerExceptionRegistry() {}

    /**
     * key: exception type。
     * value: {@link TApplicationException#getType()}, 异常类型id
     */
    private static final Map<Integer,  ThriftExceptionInformation> EXCEPTION_REPOSITORY = MapUtil.newConcurrentHashMap(4);

    static {
        ServiceLoader<ThriftExceptionInformation> thriftExceptionInformationList = ServiceLoader.load(ThriftExceptionInformation.class);
        for (ThriftExceptionInformation information : thriftExceptionInformationList) {
            addException(information.getId(), information);
        }
    }

    public static Integer getExceptionId(Class<? extends Throwable> type) {
        for (Map.Entry<Integer, ThriftExceptionInformation> entry : EXCEPTION_REPOSITORY.entrySet()) {
            ThriftExceptionInformation entryValue = entry.getValue();
            if (entryValue.getExceptionType().equals(type)) {
                return entry.getKey();
            }
        }
        return null;
    }

    public static ThriftExceptionInformation getExceptionInformation(Class<? extends Throwable> classType) {
        Integer exceptionId = getExceptionId(classType);
        if (Objects.isNull(exceptionId)) {
            return null;
        }
        return getExceptionInformation(exceptionId);
    }

    public static ThriftExceptionInformation getExceptionInformation(Integer id) {
        return EXCEPTION_REPOSITORY.get(id);
    }

    public static void addException(Integer id, ThriftExceptionInformation information) {
        if (Objects.isNull(id) || Objects.isNull(information)) {
            throw new IllegalArgumentException("ExceptionId or information should not be null.");
        }
        EXCEPTION_REPOSITORY.put(id, information);
    }


    public static ThriftCustomException writeCustomException(TProtocol outputProtocol,
                                                                  String methodName,
                                                                  int sequenceId,
                                                                  int errorCode,
                                                                  String errorMessage,
                                                                  Throwable cause) throws UnsupportedOperationException, TException {
        ThriftExceptionInformation information = EXCEPTION_REPOSITORY.get(errorCode);
        if (Objects.isNull(information)) {
            throw new UnsupportedOperationException("Not found ThriftExceptionInformation by id = " + errorCode);
        }

        ThriftCustomExceptionFactory factory = information.getFactory();
        ThriftCustomException exception = factory.createException(errorCode, errorMessage, cause);

        // Application exceptions are sent to client, and the connection can be reused
        outputProtocol.writeMessageBegin(new TMessage(methodName, TMessageType.EXCEPTION, sequenceId));
        exception.write(outputProtocol);
        outputProtocol.writeMessageEnd();
        outputProtocol.getTransport().flush();

        return exception;
    }














}
