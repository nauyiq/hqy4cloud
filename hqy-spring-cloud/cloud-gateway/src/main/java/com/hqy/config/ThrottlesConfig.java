package com.hqy.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author qy
 * @project: hqy-parent-all
 * @create 2021-07-30 17:08
 */
@Configuration
@ConfigurationProperties(prefix = "throttles")
@RefreshScope
public class ThrottlesConfig {

    private List<String> whiteIp;

    private List<String> whiteURI;

    public List<String> getWhiteIp() {
        return whiteIp;
    }

    public void setWhiteIp(List<String> whiteIp) {
        this.whiteIp = whiteIp;
    }

    public List<String> getWhiteURI() {
        return whiteURI;
    }

    public void setWhiteURI(List<String> whiteURI) {
        this.whiteURI = whiteURI;
    }
}
