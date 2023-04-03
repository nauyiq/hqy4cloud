package com.hqy.cloud.id.entities;

import com.hqy.cloud.tk.PrimaryLessBaseEntity;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.Id;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/3/23 14:19
 */
@Slf4j
@NoArgsConstructor
@AllArgsConstructor
public class SnowflakeConfig implements PrimaryLessBaseEntity {

    @Id
    private String key;
    private Integer workerId;
    private Boolean status;
    private String env;









}
