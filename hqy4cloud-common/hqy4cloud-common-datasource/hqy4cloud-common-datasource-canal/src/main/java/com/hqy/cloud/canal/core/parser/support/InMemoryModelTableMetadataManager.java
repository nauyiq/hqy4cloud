package com.hqy.cloud.canal.core.parser.support;

import cn.hutool.core.map.MapUtil;
import com.hqy.cloud.canal.annotation.CanalField;
import com.hqy.cloud.canal.annotation.CanalModel;
import com.hqy.cloud.canal.common.NamingPolicy;
import com.hqy.cloud.canal.core.parser.BaseCanalFieldConverter;
import com.hqy.cloud.canal.core.parser.ColumnMetadata;
import com.hqy.cloud.canal.core.parser.ModelTableMetadata;
import com.hqy.cloud.canal.core.parser.ModelTableMetadataManager;
import com.hqy.cloud.canal.core.parser.converter.CanalFieldConvertInput;
import com.hqy.cloud.canal.core.parser.converter.CanalFieldConvertResult;
import com.hqy.cloud.canal.core.parser.converter.CanalFieldConverterFactory;
import com.hqy.cloud.canal.model.ModelTable;
import com.hqy.cloud.util.AssertUtil;
import com.hqy.cloud.util.ReflectUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.sql.JDBCType;
import java.util.HashMap;
import java.util.Map;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/5/19 11:06
 */
@RequiredArgsConstructor(access = AccessLevel.PUBLIC, staticName = "of")
public class InMemoryModelTableMetadataManager implements ModelTableMetadataManager {
    private final Map<Class<?>, ModelTableMetadata> cache = MapUtil.newConcurrentHashMap(16);
    private final CanalFieldConverterFactory canalFieldConverterFactory;

    @Override
    public ModelTableMetadata load(Class<?> klass) {
        return cache.computeIfAbsent(klass, clazz -> {
            AssertUtil.isTrue(klass.isAnnotationPresent(CanalModel.class), String.format("[%s]没有使用@CanalModel注解", klass.getName()));
            CanalModel canalModel = klass.getAnnotation(CanalModel.class);
            NamingPolicy namingPolicy = canalModel.fieldNamingPolicy();
            ModelTableMetadata metadata = new ModelTableMetadata();
            metadata.setModelTable(ModelTable.of(canalModel.database(), canalModel.table()));
            Map<String, ColumnMetadata> fieldColumnMapping = new HashMap<>(8);
            ReflectUtils.doWithFields(klass, field -> {
                CanalField canalField;
                JDBCType sqlType = null;
                Class<? extends BaseCanalFieldConverter<?>> converterKlass = null;
                String columnName = null;
                if (field.isAnnotationPresent(CanalField.class)) {
                    canalField = field.getAnnotation(CanalField.class);
                    sqlType = canalField.sqlType();
                    converterKlass = canalField.converterKlass();
                    if (StringUtils.isNotEmpty(canalField.columnName())) {
                        columnName = canalField.columnName();
                    }
                }
                String fieldName = field.getName();
                if (null == columnName) {
                    columnName = namingPolicy.convert(fieldName);
                }
                CanalFieldConvertInput input = CanalFieldConvertInput.builder()
                        .fieldKlass(field.getType())
                        .sqlType(sqlType)
                        .converterKlass(converterKlass)
                        .build();
                CanalFieldConvertResult result = canalFieldConverterFactory.load(input);
                ColumnMetadata columnMetadata = new ColumnMetadata();
                columnMetadata.setColumnName(columnName);
                columnMetadata.setConverter(result.getConverter());
                fieldColumnMapping.put(fieldName, columnMetadata);
            });
            metadata.setFieldColumnMapping(fieldColumnMapping);
            return metadata;
        });
    }



}
