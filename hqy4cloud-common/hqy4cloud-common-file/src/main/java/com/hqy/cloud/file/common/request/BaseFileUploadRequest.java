package com.hqy.cloud.file.common.request;

import com.hqy.cloud.file.common.constants.FileAccessControl;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @author hongqy
 * @date 2025/4/3
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public abstract class BaseFileUploadRequest {

    /**
     * 文件上传场景
     */
    @NotNull(message = "上传场景不能为空")
    private String scene;

    /**
     * 文件名
     */
    @NotNull(message = "文件名不能为空")
    private String filename;

    /**
     * 唯一id
     */
    private String accessUniqueId;

    /**
     * 文件访问权限
     */
    private FileAccessControl fileAccessControl;

    /**
     * 文件元数据
     */
    private Map<String, String> metadata = new HashMap<>();

    public abstract InputStream getInputStream();

    public BaseFileUploadRequest addMetadata(String key, String value) {
        metadata.put(key, value);
        return this;
    }

}
