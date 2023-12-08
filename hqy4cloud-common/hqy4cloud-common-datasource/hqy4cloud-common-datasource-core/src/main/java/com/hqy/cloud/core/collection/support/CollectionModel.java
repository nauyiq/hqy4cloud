package com.hqy.cloud.core.collection.support;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/12/8 16:54
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CollectionModel {

    private String sql;
    private SqlType sqlType;
    private Long sqlTime;
    private Long costMills;
    private String env;
    private String reason;
    private String applicationInfo;



}
