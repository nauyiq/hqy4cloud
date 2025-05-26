package com.hqy.cloud.file.common.request;

import com.hqy.cloud.file.common.constants.FileScene;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author hongqy
 * @date 2025/5/22
 */
@Getter
@Setter
public class MultipartFileUploadRequest extends BaseFileUploadRequest {
    private MultipartFile multipartFile;
    @Override
    public InputStream getInputStream() {
        try {
            return multipartFile.getInputStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static MultipartFileUploadRequest create(MultipartFile multipartFile, String accessUnique, FileScene fileScene) {
        MultipartFileUploadRequest request = new MultipartFileUploadRequest();
        request.setMultipartFile(multipartFile);
        request.setScene(fileScene.getFolder());
        request.setFilename(multipartFile.getOriginalFilename());
        request.setFileAccessControl(fileScene.getAccessControl());
        request.setAccessUniqueId(accessUnique);
        return request;
    }
}
