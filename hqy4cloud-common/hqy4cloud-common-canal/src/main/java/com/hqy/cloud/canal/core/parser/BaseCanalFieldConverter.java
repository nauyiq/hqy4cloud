package com.hqy.cloud.canal.core.parser;

import com.hqy.cloud.canal.core.parser.converter.BinLogFieldConverter;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;

import java.sql.SQLType;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/5/19 10:47
 */
@EqualsAndHashCode
public abstract class BaseCanalFieldConverter<T> implements BinLogFieldConverter<String, T> {

    private final SQLType sqlType;
    private final Class<?> klass;

    public BaseCanalFieldConverter(SQLType sqlType, Class<?> klass) {
        this.sqlType = sqlType;
        this.klass = klass;
    }

    @Override
    public T convert(String source) {
        if (StringUtils.isBlank(source)) {
            return null;
        }
        return convertInternal(source);
    }

    /**
     * 内部转换方法
     * @param source 源字符串
     * @return T
     */
    protected abstract T convertInternal(String source);

    /**
     * 返回SQL类型
     * @return SQLType
     */
    public SQLType sqlType() {
        return sqlType;
    }

    /**
     * 返回类型
     * @return Class<?>
     */
    public Class<?> typeKlass() {
        return klass;
    }

}
