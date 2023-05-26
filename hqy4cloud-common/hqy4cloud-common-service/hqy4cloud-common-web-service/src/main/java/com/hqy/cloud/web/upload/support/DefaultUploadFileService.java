package com.hqy.cloud.web.upload.support;

import com.hqy.cloud.common.base.lang.exception.UploadFileException;
import com.hqy.cloud.util.config.ConfigurationContext;
import com.hqy.cloud.util.file.FileUtil;
import com.hqy.cloud.web.common.UploadResult;
import com.hqy.cloud.web.common.annotation.UploadMode;
import com.hqy.cloud.web.config.UploadFileProperties;
import com.hqy.cloud.web.upload.AbstractUploadFileService;
import com.hqy.cloud.web.upload.UploadResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.Future;

/**
 * 基于服务器路径的默认文件上传服务.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/9/29 15:05
 */
public class DefaultUploadFileService extends AbstractUploadFileService {

    public DefaultUploadFileService(UploadFileProperties properties) {
        super(properties);
    }

    @Override
    protected UploadResponse writeFile(String originalFilename, String folderPath, UploadMode.Mode mode, MultipartFile file) throws UploadFileException {
        String baseFileName = generateFileName(originalFilename);
        String relativeFilePath = generateRelativeFilePath(folderPath, baseFileName);
        String uploadFilePath = ConfigurationContext.getConfigPath() + relativeFilePath;
        //构建结果集
        UploadResult result = UploadResult.of(relativeFilePath, getProperties().getHostname() + relativeFilePath);
        if (mode == null || mode == UploadMode.Mode.SYNC) {
            //同步写到服务器
            FileUtil.writeToFile(file, uploadFilePath);
            return FlexibleResultUploadResponse.of(mode, result);
        } else {
            //异步写
            FlexibleResultUploadResponse.AsyncUploadFileCallFuture future = FlexibleResultUploadResponse.AsyncUploadFileCallFuture.create();
            Future<UploadResult> submit = threadPool.submit(() -> FileUtil.writeToFile(file, uploadFilePath), result);
            try {
                future.set(submit.get());
            } catch (Throwable cause) {
                future.setException(cause);
            }
            return FlexibleResultUploadResponse.of(mode, future);
        }
    }

}
