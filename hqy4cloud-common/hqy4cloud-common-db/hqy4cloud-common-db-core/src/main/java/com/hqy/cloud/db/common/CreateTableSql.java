package com.hqy.cloud.db.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/1/31
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateTableSql {

    private String table;
    private String createTable;

}
