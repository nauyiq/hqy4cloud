package com.hqy.cloud.file.common.request;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileNameUtil;
import com.hqy.cloud.file.common.constants.FileAccessControl;
import com.hqy.cloud.file.common.constants.FileScene;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @author hongqy
 * @date 2025/4/3
 */
public class FileRequestWrappers {

    public static BaseFileUploadRequest of(MultipartFile multipartFile, FileScene fileScene) throws Exception {
        return of(multipartFile, fileScene.getFolder(), fileScene.getAccessControl());
    }
    public static BaseFileUploadRequest of(File file, FileScene fileScene) throws Exception {
        return of(file, fileScene.getFolder(), fileScene.getAccessControl());
    }

    public static BaseFileUploadRequest of(MultipartFile multipartFile, String path, FileAccessControl accessControl) throws Exception {
        return of(multipartFile.getInputStream(), path, multipartFile.getOriginalFilename(), accessControl, new HashMap<>());
    }

    public static BaseFileUploadRequest of(File file, String path, FileAccessControl accessControl) throws Exception {
        return of(FileUtil.getInputStream(file), path, FileNameUtil.getName(file), accessControl, new HashMap<>());
    }

    public static BaseFileUploadRequest of(InputStream inputStream, String path, String filename, FileAccessControl accessControl, Map<String, String> metadata) {
        return new BaseFileUploadRequest() {
            @Override
            public InputStream getInputStream() {
                return inputStream;
            }
        }.setScene(path).setFilename(filename).setFileAccessControl(accessControl).setMetadata(metadata);
    }


}
