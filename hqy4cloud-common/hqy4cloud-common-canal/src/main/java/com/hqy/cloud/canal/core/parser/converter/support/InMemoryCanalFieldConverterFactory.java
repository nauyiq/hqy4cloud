package com.hqy.cloud.canal.core.parser.converter.support;

import cn.hutool.core.map.MapUtil;
import com.hqy.cloud.canal.core.parser.BaseCanalFieldConverter;
import com.hqy.cloud.canal.core.parser.converter.CanalFieldConvertInput;
import com.hqy.cloud.canal.core.parser.converter.CanalFieldConvertResult;
import com.hqy.cloud.canal.core.parser.converter.CanalFieldConverterFactory;
import com.hqy.cloud.util.JsonUtil;

import java.sql.SQLType;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/5/19 11:22
 */
@SuppressWarnings("rawtypes")
public class InMemoryCanalFieldConverterFactory implements CanalFieldConverterFactory {

    private final Set<BaseCanalFieldConverter> converters = new HashSet<>(16);

    private final Map<Class<?>, BaseCanalFieldConverter> typeKlassConverters = MapUtil.newConcurrentHashMap(16);

    private final Map<SQLType, BaseCanalFieldConverter> sqlTypeConverters = MapUtil.newConcurrentHashMap(16);

    private final Map<Class<? extends BaseCanalFieldConverter>, BaseCanalFieldConverter> klassConverters = MapUtil.newConcurrentHashMap(16);

    public static CanalFieldConverterFactory of() {
        return new InMemoryCanalFieldConverterFactory();
    }

    private InMemoryCanalFieldConverterFactory() {
        loadInternalConverters();
    }

    private void loadInternalConverters() {
        converters.add(NullCanalFieldConverter.X);
        converters.add(BigIntCanalFieldConverter.X);
        converters.add(VarcharCanalFieldConverter.X);
        converters.add(IntCanalFieldConverter.X);
        converters.add(DecimalCanalFieldConverter.X);
        converters.add(TinyIntCanalFieldConverter.X);
        converters.add(TimestampCanalFieldConverter0.X);
        converters.add(SqlDateCanalFieldConverter0.X);
        converters.add(TimestampCanalFieldConverter1.X);
        converters.add(SqlDateCanalFieldConverter1.X);
        converters.add(TimestampCanalFieldConverter2.X);
        converters.forEach(converter -> registerConverter(converter, true));
    }

    @Override
    public void registerConverter(BaseCanalFieldConverter<?> converter, boolean replace) {
        if (replace) {
            typeKlassConverters.put(converter.typeKlass(), converter);
            sqlTypeConverters.put(converter.sqlType(), converter);
            klassConverters.put(converter.getClass(), converter);
        } else {
            typeKlassConverters.putIfAbsent(converter.typeKlass(), converter);
            sqlTypeConverters.putIfAbsent(converter.sqlType(), converter);
            klassConverters.putIfAbsent(converter.getClass(), converter);
        }
    }

    @Override
    public CanalFieldConvertResult load(CanalFieldConvertInput input) {
        BaseCanalFieldConverter<?> converter;
        if (null != input.getSqlType() && null != (converter = sqlTypeConverters.get(input.getSqlType()))) {
            return CanalFieldConvertResult.builder().converter(converter).build();
        }
        if (null != input.getConverterKlass() && null != (converter = klassConverters.get(input.getConverterKlass()))) {
            return CanalFieldConvertResult.builder().converter(converter).build();
        }
        if (null != input.getFieldKlass() && null != (converter = typeKlassConverters.get(input.getFieldKlass()))) {
            return CanalFieldConvertResult.builder().converter(converter).build();
        }
        throw new IllegalArgumentException(String.format("加载Canal类型转换器失败,输入参数:%s", JsonUtil.toJson(input)));
    }

}
