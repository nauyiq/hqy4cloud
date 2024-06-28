package com.hqy.cloud.file.core;

import com.hqy.cloud.common.base.lang.exception.UploadFileException;
import com.hqy.cloud.file.api.AbstractUploadFileService;
import com.hqy.cloud.file.common.MultipartFileAdaptor;
import com.hqy.cloud.file.common.annotation.UploadMode;
import com.hqy.cloud.file.common.result.UploadResponse;
import com.hqy.cloud.file.common.result.UploadResult;
import com.hqy.cloud.file.config.UploadFileProperties;
import com.hqy.cloud.util.config.ConfigurationContext;
import com.hqy.cloud.file.common.utils.FileUtil;
import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.CompletableFuture;

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
    protected UploadResponse writeFile(String originalFilename, String folderPath, UploadContext.UploadState state, MultipartFile file) throws UploadFileException {
        String baseFileName = generateFileName(originalFilename);
        String relativeFilePath = generateRelativeFilePath(folderPath, baseFileName);
        String uploadFilePath = ConfigurationContext.getConfigPath() + relativeFilePath;
        //构建结果集
        UploadResult result = UploadResult.of(relativeFilePath, getProperties().getHostname() + relativeFilePath);
        UploadMode.Mode mode = state.getMode();
        if (mode == null || mode == UploadMode.Mode.SYNC) {
            //同步写到服务器
            FileUtil.writeToFile(file, uploadFilePath);
            return DefaultResultUploadResponse.of(mode, result);
        } else {
            try {
                // MultipartFile 对象在文件上传的时候，会生成 临时文件，此时生成的临时文件在主线程中；而异步线程在操作 MultipartFile 对象时，此时主线程中的临时文件，将会被 spring 给删除了，也就造成异常 FileNotFound！
                // 采用数组拷贝的方式，讲文件内容拷贝的新的MultipartFileAdaptor适配类中.
                final MultipartFile uploadFile;
                if (state.isCopyFileContent()) {
                    byte[] newBytes = file.getBytes().clone();
                    uploadFile = new MultipartFileAdaptor(originalFilename, newBytes);
                } else {
                    uploadFile = file;
                }
                CompletableFuture<UploadResult> future = CompletableFuture.supplyAsync(() -> {
                    FileUtil.writeToFile(uploadFile, uploadFilePath);
                    return result;
                }, threadPool);
                return DefaultResultUploadResponse.of(mode, result, future);
            } catch (Throwable cause) {
                throw new UploadFileException(cause);
            }

        }
    }

}
