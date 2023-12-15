package com.hqy.cloud.actuator.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/12/15 13:50
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ConfigurationProperties(prefix = "com.hqy.cloud.switchers")
public class SwitcherConfigProperties {

    private List<Config> configs;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Config {
        private Integer id;
        private Boolean enabled;
    }



}
