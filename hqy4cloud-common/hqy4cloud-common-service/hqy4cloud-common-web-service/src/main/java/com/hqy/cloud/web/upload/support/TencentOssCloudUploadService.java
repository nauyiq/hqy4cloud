package com.hqy.cloud.web.upload.support;

import cn.hutool.core.util.StrUtil;
import com.hqy.cloud.common.base.lang.StringConstants;
import com.hqy.cloud.common.base.lang.exception.UploadFileException;
import com.hqy.cloud.util.AssertUtil;
import com.hqy.cloud.web.common.annotation.UploadMode;
import com.hqy.cloud.web.config.UploadFileProperties;
import com.hqy.cloud.web.upload.AbstractUploadFileService;
import com.hqy.cloud.web.upload.UploadResponse;
import com.hqy.foundation.common.bind.CloudSecret;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.event.ProgressEvent;
import com.qcloud.cos.event.ProgressListener;
import com.qcloud.cos.exception.CosClientException;
import com.qcloud.cos.http.HttpProtocol;
import com.qcloud.cos.model.Bucket;
import com.qcloud.cos.model.CannedAccessControlList;
import com.qcloud.cos.model.CreateBucketRequest;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.region.Region;
import com.qcloud.cos.transfer.TransferManager;
import com.qcloud.cos.transfer.TransferManagerConfiguration;
import com.qcloud.cos.transfer.Upload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

import static com.hqy.cloud.web.common.Constants.*;

/**
 * 腾讯oss cloud文件上传服务.
 * @see AbstractUploadFileService
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/5/25 10:12
 */
@Slf4j
public class TencentOssCloudUploadService extends AbstractUploadFileService implements DisposableBean {
    private final String bucketName;
    private final String hostname;
    private final TransferManager transferManager;

    public TencentOssCloudUploadService(String bucketName, UploadFileProperties properties) {
        super(properties);
        CloudSecret cloudSecret = properties.getTencent();
        AssertUtil.isFalse(Objects.isNull(cloudSecret) || StringUtils.isAnyBlank(cloudSecret.getSecretId(), cloudSecret.getSecretKey())
                , "Tencent secret should not be empty.");
        // create cos client
        this.transferManager = createTransferManager(properties, cloudSecret);
        if (StringUtils.isBlank(bucketName)) {
            this.bucketName = "file" + StrUtil.DASHED + cloudSecret.getAppId();
        } else {
            this.bucketName = bucketName + StrUtil.DASHED + cloudSecret.getAppId();
        }
        // setting tencent oss hostname.
        this.hostname = getTencentHostName(cloudSecret);
        // create bucket
        createdBucket();
    }

    private String getTencentHostName(CloudSecret cloudSecret) {
        String regionName = cloudSecret.getProperties().getOrDefault(REGION, TENCENT_DEFAULT_REGION);
        String appId = cloudSecret.getAppId();
        return StringConstants.Host.HTTPS +
                this.bucketName +
                StrUtil.DOT +
                "cos" +
                StrUtil.DOT +
                regionName +
                StrUtil.DOT +
                "myqcloud.com";
    }

    private TransferManager createTransferManager(UploadFileProperties properties, CloudSecret cloudSecret) {
        // 自定义线程池大小，建议在客户端与 COS 网络充足（例如使用腾讯云的 CVM，同地域上传 COS）的情况下，设置成16或32即可，可较充分的利用网络资源
        // 对于使用公网传输且网络带宽质量不高的情况，建议减小该值，避免因网速过慢，造成请求超时。
        // 传入一个 threadpool, 若不传入线程池，默认 TransferManager 中会生成一个单线程的线程池。
        TransferManager transferManager = new TransferManager(createCosClient(cloudSecret), threadPool);
        // 设置高级接口的配置项
        // 分块上传阈值和分块大小
        TransferManagerConfiguration transferManagerConfiguration = new TransferManagerConfiguration();
        transferManagerConfiguration.setMultipartUploadThreshold(properties.getSize().toMillis());
        transferManagerConfiguration.setMinimumUploadPartSize(properties.getSize().toMillis() / 4);
        transferManager.setConfiguration(transferManagerConfiguration);
        return transferManager;
    }

    private COSClient createCosClient(CloudSecret cloudSecret) {
        // 1 初始化用户身份信息（secretId, secretKey）。
        String secretId = cloudSecret.getSecretId();
        String secretKey = cloudSecret.getSecretKey();
        COSCredentials cred = new BasicCOSCredentials(secretId, secretKey);
        // 2 设置 bucket 的地域, COS 地域的简称请参见 https://cloud.tencent.com/document/product/436/6224
        // clientConfig 中包含了设置 region, https(默认 http), 超时, 代理等 set 方法, 使用可参见源码或者常见问题 Java SDK 部分。
        String regionName = cloudSecret.getProperties().getOrDefault(REGION, TENCENT_DEFAULT_REGION);
        Region region = new Region(regionName);
        ClientConfig clientConfig = new ClientConfig(region);
        // 从 5.6.54 版本开始，默认使用了 https
        clientConfig.setHttpProtocol(HttpProtocol.https);
        return new COSClient(cred, clientConfig);
    }

    private void createdBucket() {
        CreateBucketRequest createBucketRequest = new CreateBucketRequest(this.bucketName);
        // 设置 bucket 的权限为 Private(私有读写)、其他可选有 PublicRead（公有读私有写）、PublicReadWrite（公有读写）
        createBucketRequest.setCannedAcl(CannedAccessControlList.Private);
        try{
            Bucket bucketResult = this.transferManager.getCOSClient().createBucket(createBucketRequest);
        } catch (CosClientException cosClientException) {
            if (TENCENT_BUCKET_ALREADY_EXIST.equals(cosClientException.getErrorCode())) {
                //已经存在 bucket
                log.info("The bucket: {} is already exist.", this.bucketName);
            } else {
                log.error("Failed execute to create bucket: {}, cause: {}.", this.bucketName, cosClientException.getMessage(), cosClientException);
            }
        } catch (Throwable cause) {
            log.error("Failed execute to create bucket: {}, cause: {}.", this.bucketName, cause.getMessage(), cause);
        }
    }

    @Override
    protected UploadResponse writeFile(String originalFilename, String folderPath, UploadContext.UploadState state, final MultipartFile file) throws UploadFileException {
        // 指定文件上传到 COS 上的路径，即对象键。例如对象键为 folder/picture.jpg，则表示将文件 picture.jpg 上传到 folder 路径下
        if (!folderPath.trim().endsWith(StrUtil.SLASH)) {
            folderPath = folderPath.concat(StrUtil.SLASH);
        }
        // 构建结果集
        String fileName = generateFileName(originalFilename);
        String relativeFilePath = folderPath + fileName;
        String path = this.hostname + relativeFilePath;
        com.hqy.cloud.web.common.UploadResult result = com.hqy.cloud.web.common.UploadResult.of(relativeFilePath, path);

        // 获取输入流.
        try (InputStream inputStream = getInputStream(state, originalFilename, file)) {
            PutObjectRequest putObjectRequest = new PutObjectRequest(this.bucketName, relativeFilePath, inputStream, null);
            //同步上传
            UploadMode.Mode mode = state.getMode();
            if (mode == null || mode == UploadMode.Mode.SYNC) {
                Upload upload = transferManager.upload(putObjectRequest);
                upload.waitForUploadResult();
                return FlexibleResultUploadResponse.of(mode, result);
            } else {
                //异步上传
                FlexibleResultUploadResponse.AsyncUploadFileCallFuture future = FlexibleResultUploadResponse.AsyncUploadFileCallFuture.create();
                try {
                    Upload upload = transferManager.upload(putObjectRequest);
                } catch (Throwable cause) {
                    future.setException(cause);
                }
                putObjectRequest.withGeneralProgressListener(PutObjectEndProgressListener.of(file.getSize(), inputStream, future, result));
                return FlexibleResultUploadResponse.of(mode, result, future);
            }
        } catch (Throwable cause) {
           throw new UploadFileException(cause);
        }
    }

    private InputStream getInputStream(UploadContext.UploadState state, String originalFilename, MultipartFile file) throws IOException {
        InputStream inputStream;
        if (state.isCopyFileContent()) {
            MultipartFileAdaptor multipartFile = new MultipartFileAdaptor(originalFilename, file.getBytes().clone());
            inputStream = multipartFile.getInputStream();
        } else {
            inputStream = file.getInputStream();
        }
        return inputStream;
    }

    public TransferManager getTransferManager() {
        return transferManager;
    }

    @Override
    public void destroy() throws Exception {
        this.transferManager.shutdownNow();
    }

    @RequiredArgsConstructor(staticName = "of")
    public static class PutObjectEndProgressListener implements ProgressListener {
        private final long bytes;
        private final InputStream inputStream;
        private final AtomicLong uploadedBytes = new AtomicLong(0);
        private final FlexibleResultUploadResponse.AsyncUploadFileCallFuture future;
        private final com.hqy.cloud.web.common.UploadResult uploadResult;

        @Override
        public void progressChanged(ProgressEvent progressEvent) {
            //文件上传完毕
            if (uploadedBytes.addAndGet(progressEvent.getBytes()) == bytes) {
                try {
                    this.future.set(uploadResult);
                } finally {
                    if (inputStream != null) {
                        IOUtils.closeQuietly(inputStream);
                    }
                }

            }
        }
    }

}
