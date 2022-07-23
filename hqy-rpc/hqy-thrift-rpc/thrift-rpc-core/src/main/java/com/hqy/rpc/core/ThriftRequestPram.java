package com.hqy.rpc.core;

import cn.hutool.core.map.MapUtil;
import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.codec.ThriftStruct;
import com.hqy.base.common.base.lang.StringConstants;

import java.util.Map;

/**
 * 在rpc调用过程中 动态拼接参数类 用于拓展 识别当前调用链. 标志oneway等 <br>
 * 在thrift.rpc、nifty中 oneway并不能区分同步调用还是异步调用 oneway是nifty的一个特性<br>
 * 在apache.thrift中 oneway表示客户端是否关心返回结果。 oneway=true 表示不关心返回结果， 此时可以稍微提升接口性能. 当不可以表示当前就是异步调用.
 *
 * 因此在此框架中。 修改nifty源码。 在rpc调用中oneway = true 的rpc方法改为异步调用.
 *
 * 每个transaction中 有参数:    RootId，用于标识唯一的一个调用链;
 *                           ParentId，父Id是谁？谁在调用我；
 *                           ChildId，我在调用谁?
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/4/21 18:01
 */
@ThriftStruct
public final class ThriftRequestPram {

    @ThriftField(1)
    public final Map<String, String> pram;

    public ThriftRequestPram() {
        pram = MapUtil.newHashMap();
    }

    public ThriftRequestPram(Map<String, String> pram) {
        this.pram = pram;
    }

    public String getParameter(String key) {
        return getParameter(key, StringConstants.EMPTY);
    }

    public String getParameter(String key, String defaultValue) {
        if (!isValidParams()) {
            return defaultValue;
        }
        return pram.getOrDefault(key, defaultValue);
    }

    public boolean isValidParams() {
        return !this.pram.isEmpty();
    }


}
