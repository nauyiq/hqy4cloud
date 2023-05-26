package com.hqy.cloud.web.upload;

import com.hqy.cloud.web.common.UploadResult;
import com.hqy.cloud.web.common.annotation.UploadMode;

/**
 * 文件上传响应类.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/9/29 10:23
 */
public interface UploadResponse {

    /**
     * 获取当前文件上传的模式？ 同步或异步
     * @return {@link UploadMode.Mode}
     */
    UploadMode.Mode uploadMode();

    /**
     * 获取文件上传的结果.
     * @return {@link UploadResult}
     */
    UploadResult getResult();






}
