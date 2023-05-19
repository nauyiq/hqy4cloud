package com.hqy.cloud.canal.core.parser;

import com.hqy.cloud.canal.model.ModelTable;
import lombok.Data;

import java.util.Map;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/5/19 10:45
 */
@Data
public class ModelTableMetadata {

    private ModelTable modelTable;

    /**
     * fieldName -> ColumnMetadata
     */
    private Map<String, ColumnMetadata> fieldColumnMapping;

}
