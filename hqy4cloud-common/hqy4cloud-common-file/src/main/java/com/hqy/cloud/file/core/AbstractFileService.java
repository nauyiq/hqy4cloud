package com.hqy.cloud.file.core;

import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.hqy.cloud.file.api.FileService;
import com.hqy.cloud.file.check.FileUploadCheckerChain;
import com.hqy.cloud.file.common.FileException;
import com.hqy.cloud.file.common.request.BaseFileUploadRequest;
import com.hqy.cloud.file.common.request.FileUploadRequest;
import com.hqy.cloud.file.common.response.FileResponse;
import com.hqy.cloud.file.common.result.FileResultCode;
import com.hqy.cloud.util.CommonDateUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.File;

/**
 * 文件服务基类
 * @author hongqy
 * @date 2025/4/3
 */
@Slf4j
public abstract class AbstractFileService implements FileService {

    @Resource
    private FileUploadCheckerChain fileUploadCheckerChain;

    @Override
    public FileResponse uploadFile(BaseFileUploadRequest baseFileUploadRequest) {
        Assert.isTrue(baseFileUploadRequest == null || baseFileUploadRequest.getInputStream() == null, () -> new FileException(FileResultCode.FILE_IS_EMPTY));

        // 文件检查
        fileUploadCheckerChain.check(baseFileUploadRequest);

        // 路径生成
        String path = getFilePath(baseFileUploadRequest);
        String filename = getFilename(baseFileUploadRequest);
        log.info("文件上传开始, path：{}, filename:{}", path, filename);
        FileUploadRequest request = new FileUploadRequest(path, filename, baseFileUploadRequest.getInputStream(), baseFileUploadRequest.getFileAccessControl());
        // 子类实现上传逻辑
        return uploadFile(request);
    }

    protected abstract FileResponse uploadFile(FileUploadRequest fileUploadRequest);

    protected String getFilePath(BaseFileUploadRequest baseFileUploadRequest) {
        String scene = baseFileUploadRequest.getScene();
        String accessUniqueId = baseFileUploadRequest.getAccessUniqueId();
        // 生成唯一目录
        return CommonDateUtil.today() + scene + (StringUtils.isBlank(accessUniqueId) ? StrUtil.EMPTY : File.separator + accessUniqueId);
    }

    protected String getFilename(BaseFileUploadRequest baseFileUploadRequest) {
        String filename = baseFileUploadRequest.getFilename();
        String suffix = FileNameUtil.getSuffix(filename);
        return IdUtil.fastSimpleUUID() + StrUtil.DOT + suffix;
    }



}
