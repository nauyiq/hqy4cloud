/*
 * Copyright (C) 2013 Facebook, Inc.
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

import com.facebook.nifty.core.RequestContext;
import com.facebook.swift.codec.ThriftCodec;
import org.apache.thrift.TException;

import java.util.ArrayList;
import java.util.List;

public class ContextChain {
    private final List<ThriftEventHandler> handlers;
    private final String methodName;
    private final List<Object> contexts;

    ContextChain(List<ThriftEventHandler> handlers,String simpleMethodName, String methodName, String serviceTypeName, RequestContext requestContext) {
        this.handlers = handlers;
        this.methodName = methodName;
        this.contexts = new ArrayList<>();
        for (ThriftEventHandler h : this.handlers) {
            this.contexts.add(h.getContext(simpleMethodName, methodName, serviceTypeName, requestContext));
        }
    }

    public void preRead() throws TException {
        for (int i = 0; i < handlers.size(); i++) {
            handlers.get(i).preRead(contexts.get(i), methodName);
        }
    }

    public void postRead(Object[] args) throws TException {
        for (int i = 0; i < handlers.size(); i++) {
            handlers.get(i).postRead(contexts.get(i), methodName, args);
        }
    }
    public void preInvokeMethod(Object[] args) throws Exception {
        for (int i = 0; i < handlers.size(); i++) {
            handlers.get(i).preInvokeMethod(contexts.get(i), methodName, args);
        }
    }

    public void preWrite(Object result) throws TException {
        for (int i = 0; i < handlers.size(); i++) {
            handlers.get(i).preWrite(contexts.get(i), methodName, result);
        }
    }

    public void preWriteException(Throwable t) throws TException {
        for (int i = 0; i < handlers.size(); i++) {
            handlers.get(i).preWriteException(contexts.get(i), methodName, t);
        }
    }

    public void postWrite(Object result) throws TException {
        for (int i = 0; i < handlers.size(); i++) {
            handlers.get(i).postWrite(contexts.get(i), methodName, result);
        }
    }



    public void postWriteException(Throwable t) throws TException {
        for (int i = 0; i < handlers.size(); i++) {
            handlers.get(i).postWriteException(contexts.get(i), methodName, t);
        }
    }

    public void declaredUserException(Throwable t, ThriftCodec<?> exceptionCodec) throws TException {
        for (int i = 0; i < handlers.size(); i++) {
            handlers.get(i).declaredUserException(contexts.get(i), methodName, t, exceptionCodec);
        }
    }

    public void undeclaredUserException(Throwable t) throws TException {
        for (int i = 0; i < handlers.size(); i++) {
            handlers.get(i).undeclaredUserException(contexts.get(i), methodName, t);
        }
    }

    public void done() {
        for (int i = 0; i < handlers.size(); i++) {
            handlers.get(i).done(contexts.get(i), methodName);
        }
    }
}
