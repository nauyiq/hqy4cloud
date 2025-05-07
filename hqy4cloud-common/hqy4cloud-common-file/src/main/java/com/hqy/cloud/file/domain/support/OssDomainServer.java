package com.hqy.cloud.file.domain.support;

import cn.hutool.core.lang.Assert;
import com.hqy.cloud.file.common.CloudSecret;
import com.hqy.cloud.file.config.UploadFileProperties;
import com.hqy.cloud.file.domain.DomainServer;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.File;

/**
 * @author hongqy
 * @date 2025/5/7
 */
@Slf4j
@RequiredArgsConstructor
public class OssDomainServer implements DomainServer {
    @Resource
    private UploadFileProperties uploadFileProperties;

    @Override
    public String getDomain(String env, String scene) {
        CloudSecret oss = uploadFileProperties.getOss();
        Assert.notNull(oss, "获取oss配置为空");
        String bucket = oss.getBucket();
        String endpoint = oss.getEndpoint();
        return endpoint + File.separator + bucket + scene;
    }
}
