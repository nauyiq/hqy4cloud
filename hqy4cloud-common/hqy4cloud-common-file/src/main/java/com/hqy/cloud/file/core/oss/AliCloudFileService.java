package com.hqy.cloud.file.core.oss;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.common.auth.CredentialsProvider;
import com.aliyun.oss.common.auth.DefaultCredentialProvider;
import com.aliyun.oss.common.comm.ResponseMessage;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.PutObjectResult;
import com.hqy.cloud.common.base.lang.StringConstants;
import com.hqy.cloud.file.common.CloudSecret;
import com.hqy.cloud.file.common.FileException;
import com.hqy.cloud.file.common.request.FileQueryRequest;
import com.hqy.cloud.file.common.request.FileUploadRequest;
import com.hqy.cloud.file.common.response.FileResponse;
import com.hqy.cloud.file.common.result.FileResultCode;
import com.hqy.cloud.file.config.UploadFileProperties;
import com.hqy.cloud.file.core.AbstractFileService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.InputStream;

/**
 * 阿里云文件服务
 * @author hongqy
 * @date 2025/5/6
 */
@Slf4j
public class AliCloudFileService extends AbstractFileService {
    @Resource
    private UploadFileProperties properties;

    @Override
    protected FileResponse uploadFile(FileUploadRequest fileUploadRequest) {
        CloudSecret cloudSecret = properties.getOss();
        Assert.notNull(cloudSecret, "获取oss配置为空");

        // 创建oss客户端
        OSS ossClient = createOssClient(cloudSecret);

        String path = fileUploadRequest.getPath();
        // 桶名称
        String bucket = cloudSecret.getBucket();
        String bucketPrefix = bucket + File.separator;
        // 填写Object完整路径，完整路径中不能包含Bucket名称, 例如exampledir/exampleobject.txt。
        path = path.startsWith(bucketPrefix) ? path.replace(bucketPrefix, StrUtil.EMPTY) : path;
        try {
            // 创建PutObjectRequest对象。
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, path, fileUploadRequest.getInputStream());
            // 上传字符串。
            PutObjectResult result = ossClient.putObject(putObjectRequest);
            if (StringUtils.isNotBlank(result.getRequestId())) {
                ResponseMessage response = result.getResponse();
                String uri = response.getUri();
                return FileResponse.ok(uri, path, response.getContent());
            }
            throw new FileException(FileResultCode.UPLOAD_FAILED);
        } catch (Exception e) {
            log.error("【文件上传】阿里云oss上传异常， request:{}", fileUploadRequest);
            throw new FileException(FileResultCode.UPLOAD_EXCEPTION);
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }


    @Override
    public FileResponse getFile(FileQueryRequest fileQueryRequest) {
        CloudSecret cloudSecret = properties.getOss();
        Assert.notNull(cloudSecret, "获取oss配置为空");

        // 创建oss客户端
        OSS ossClient = createOssClient(cloudSecret);
        // 桶名称
        String bucket = cloudSecret.getBucket();
        String accessUri = fileQueryRequest.getAccessUri();
        // 访问路径截取桶名称 获取桶对象路径
        String objectName = buildObjectName(bucket, accessUri);
        try {
            OSSObject object = ossClient.getObject(bucket, objectName);
            InputStream contentStream = object.getObjectContent();
            return FileResponse.ok(accessUri, objectName, contentStream);
        } catch (Exception e) {
            log.error("【文件下载】阿里云oss文件下载异常， request:{}", fileQueryRequest);
            throw new FileException(FileResultCode.DOWNLOAD_FAILED);
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }

    private String buildObjectName(String bucket, String accessUri) {
        if (!accessUri.contains(bucket)) {
            // 不包含bucket 截掉http开头的域名
            return accessUri.startsWith(StringConstants.HTTP) ? accessUri.substring(accessUri.indexOf("/") + 2) : accessUri;
        } else {
            return accessUri.substring(accessUri.indexOf(bucket) + bucket.length() + 1);
        }
    }
    private OSS createOssClient(CloudSecret cloudSecret) {
        // 使用代码嵌入的RAM用户的访问密钥配置访问凭证。
        CredentialsProvider credentialsProvider = new DefaultCredentialProvider(cloudSecret.getSecretId(), cloudSecret.getSecretKey());
        // 创建OSSClient实例。
        return new OSSClientBuilder().build(cloudSecret.getEndpoint(), credentialsProvider);
    }

}
