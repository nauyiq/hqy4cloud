package com.hqy.cloud.coll.entity;

import com.hqy.cloud.datasource.core.SqlExceptionType;
import com.hqy.cloud.db.tk.model.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Table;

/**
 * sql记录表entity
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/12/11 9:25
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "t_sql_record")
@EqualsAndHashCode(callSuper = true)
public class SqlRecord extends BaseEntity<Long> {

    /**
     * 应用名
     */
    private String application;

    /**
     * 类型 {@link SqlExceptionType}
     */
    private Integer type;

    /**
     * sql开始时间
     */
    private Long startTime;

    /**
     * sql耗时
     */
    private Long costMills;

    /**
     * sql参数
     */
    private String params;

    /**
     * 异常的原因
     */
    private String reason;

    /**
     * 运行环境
     */
    private String env;

    /**
     * 执行的sql
     */
    @Column(name = "`sql`")
    private String sql;


}
