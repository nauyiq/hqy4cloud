package com.hqy.cloud.web.config;

import com.hqy.cloud.common.base.lang.StringConstants;
import com.hqy.foundation.common.bind.CloudSecret;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

import static com.hqy.cloud.web.upload.UploadFileService.DEFAULT_FOLDER;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/5/25 10:24
 */
@Data
@ConfigurationProperties(prefix = "hqy4cloud.upload")
public class UploadFileProperties {

    /**
     * 文件上传hostname
     * 默认使用硬编码的域名， 即https://files.hongqy1024.cn
     */
    private String hostname = StringConstants.Host.HTTPS_FILE_ACCESS;

    /**
     * 文件上传folder
     * 文件上传文件夹， 默认为/files/common
     */
    private String folder = DEFAULT_FOLDER;

    /**
     * 异步线程池的线程个数.
     * 文件上传 默认为IO密集型 最大线程数默认设置为 2n + 1
     */
    private Integer maxThreadCore = Runtime.getRuntime().availableProcessors() * 2 + 1;

    /**
     * 单个文件的最大size单位
     * 默认为4MB 即 4 * 1024 * 1000
     */
    private Duration size = Duration.ofMillis(4 * 1024 * 1000L);

    /**
     * 总文件的最大size
     * 默认为20MB 即 20 * 1024 * 1000
     */
    private Duration maxSize = Duration.ofMillis(20 * 1024 * 1000L);

    /**
     * 腾讯云 oss
     */
    private CloudSecret tencent;

    /**
     * 阿里云 oss
     */
    private CloudSecret ali;

    /**
     * 华为 oss
     */
    private CloudSecret huawei;







}
