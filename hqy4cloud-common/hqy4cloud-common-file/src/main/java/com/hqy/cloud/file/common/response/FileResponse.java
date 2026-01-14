package com.hqy.cloud.file.common.response;

import com.hqy.cloud.common.response.Response;
import com.hqy.cloud.common.result.Result;
import lombok.Getter;
import lombok.Setter;

import java.io.InputStream;

/**
 * @author hongqy
 * @date 2025/4/3
 */
@Getter
@Setter
public class FileResponse extends Response {

    /**
     * 上传文件的访问路径
     */
    private String path;

    /**
     * 相对路径
     */
    private String relativePath;

    /**
     * 文件流
     */
    private InputStream inputStream;

    public static FileResponse ok() {
        FileResponse fileResponse = new FileResponse();
        fileResponse.setSuccess(true);
        return fileResponse;
    }

    public static FileResponse ok(String path, String relativePath) {
         return ok(path, relativePath, null);
    }

    public static FileResponse ok(String path, String relativePath, InputStream inputStream) {
        FileResponse fileResponse = new FileResponse();
        fileResponse.setSuccess(true);
        fileResponse.setPath(path);
        fileResponse.setRelativePath(relativePath);
        fileResponse.setInputStream(inputStream);
        return fileResponse;
    }

    public static FileResponse failed(Result result) {
        FileResponse fileResponse = new FileResponse();
        fileResponse.setSuccess(false);
        fileResponse.setCode(result.getCode());
        fileResponse.setMessage(result.getMessage());
        return fileResponse;
    }

}
