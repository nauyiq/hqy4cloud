package com.hqy.cloud.canal.core.parser.support;

import com.hqy.cloud.canal.core.parser.BaseCommonEntryFunction;
import com.hqy.cloud.canal.core.parser.ColumnMetadata;
import com.hqy.cloud.canal.core.parser.ModelTableMetadata;
import com.hqy.cloud.util.AssertUtil;
import com.hqy.cloud.util.ClassUtils;
import com.hqy.cloud.util.ReflectUtils;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.Objects;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/5/19 11:17
 */
@Slf4j
@Getter
@RequiredArgsConstructor(access = AccessLevel.PUBLIC, staticName = "of")
public class ReflectionBinLogEntryFunction<T> extends BaseCommonEntryFunction<T> {
    private final Class<T> klass;
    private final ModelTableMetadata modelTableMetadata;

    @Override
    public T apply(Map<String, String> data) {
        Constructor<T> constructor = ClassUtils.getConstructorIfAvailable(klass);
        if (Objects.nonNull(constructor)) {
            try {
                T instance = constructor.newInstance();
                Map<String, ColumnMetadata> fieldColumnMapping = modelTableMetadata.getFieldColumnMapping();
                ReflectUtils.doWithFields(klass, field -> {
                    String fieldName = field.getName();
                    ColumnMetadata columnMetadata = fieldColumnMapping.get(fieldName);
                    // 理论上这里不会为NULL,谨慎起见避免低端NPE做个断言
                    AssertUtil.notNull(columnMetadata, String.format("[%s.%s]属性获取列属性元数据失败", klass.getSimpleName(), fieldName));
                    String value = data.get(columnMetadata.getColumnName());
                    if (null != value) {
                        Object convertValue = columnMetadata.getConverter().convert(value);
                        // 抑制修饰符和值设置
                        ReflectUtils.makeAccessible(field);
                        field.set(instance, convertValue);
                    }
                });
                return instance;
            } catch (Exception e) {
                throw new IllegalArgumentException(e);
            }
        }
        throw new IllegalArgumentException(String.format("基于类型[%s]实例化和反射赋值失败,请确定是否提供了默认的构造函数", klass.getName()));
    }


}
