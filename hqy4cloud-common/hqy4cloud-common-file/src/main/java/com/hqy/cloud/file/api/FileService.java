package com.hqy.cloud.file.api;

import com.hqy.cloud.file.common.request.BaseFileUploadRequest;
import com.hqy.cloud.file.common.request.FileQueryRequest;
import com.hqy.cloud.file.common.response.FileResponse;

/**
 * 文件服务
 * @author hongqy
 * @date 2025/4/3
 */
public interface FileService {

    /**
     * 获取文件
     * @param fileQueryRequest 文件查询请求
     * @return
     */
    FileResponse getFile(FileQueryRequest fileQueryRequest);

    /**
     * 文件上传请求
     * @param baseFileUploadRequest 文件上传请求
     * @return
     */
    FileResponse uploadFile(BaseFileUploadRequest baseFileUploadRequest);



}
