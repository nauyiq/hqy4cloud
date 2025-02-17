package com.hqy.cloud.auth.infrastructure.certification.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author hongqy
 * @date 2025/2/14
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "spring.auth")
public class CertificationConfigProperties {

    private String host;

    private String path;

    private String appcode;

}
