package com.hqy.cloud.web.upload.support;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.hqy.cloud.common.base.lang.exception.UploadFileException;
import com.hqy.cloud.util.config.ConfigurationContext;
import com.hqy.cloud.util.file.FileUtil;
import com.hqy.cloud.web.common.UploadResult;
import com.hqy.cloud.web.common.annotation.UploadMode;
import com.hqy.cloud.web.config.UploadFileProperties;
import com.hqy.cloud.web.upload.AbstractUploadFileService;
import com.hqy.cloud.web.upload.UploadResponse;
import org.apache.commons.io.IOUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.concurrent.Future;

/**
 * 基于服务器路径的默认文件上传服务.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/9/29 15:05
 */
public class DefaultUploadFileService extends AbstractUploadFileService {
    private final ListeningExecutorService executorService;

    public DefaultUploadFileService(UploadFileProperties properties) {
        super(properties);
        this.executorService = MoreExecutors.listeningDecorator(threadPool);
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
            return FlexibleResultUploadResponse.of(mode, result);
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
                ListenableFuture<UploadResult> listenableFuture = executorService.submit(() -> FileUtil.writeToFile(uploadFile, uploadFilePath), result);
                return FlexibleResultUploadResponse.of(mode, result, listenableFuture);
            } catch (Throwable cause) {
                throw new UploadFileException(cause);
            }

        }
    }

}
