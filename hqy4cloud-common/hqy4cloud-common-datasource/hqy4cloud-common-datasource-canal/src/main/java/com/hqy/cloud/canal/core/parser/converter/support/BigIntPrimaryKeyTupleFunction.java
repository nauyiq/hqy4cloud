package com.hqy.cloud.canal.core.parser.converter.support;

import com.hqy.cloud.canal.core.parser.BasePrimaryKeyTupleFunction;

import java.util.Map;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/5/19 10:52
 */
public class BigIntPrimaryKeyTupleFunction extends BasePrimaryKeyTupleFunction {

    /**
     * 单例
     */
    public static final BasePrimaryKeyTupleFunction X = new BigIntPrimaryKeyTupleFunction();

    private BigIntPrimaryKeyTupleFunction() {
    }

    @Override
    public Long apply(Map<String, String> before, Map<String, String> after, String primaryKey) {
        String temp;
        if (null != after && null != (temp = after.get(primaryKey))) {
            return BigIntCanalFieldConverter.X.convert(temp);
        }
        return null;
    }

}
