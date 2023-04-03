package com.hqy.web.service.support;

import com.hqy.foundation.common.FileResponse;
import com.hqy.cloud.util.file.FileUtil;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/9/29 15:05
 */
public class DefaultUploadFileService extends AbstractUploadFileService{

    /**
     * max file size.
     */
    private int maxFileSizeBytes;

    public DefaultUploadFileService(String accessHost) {
        this(accessHost, 4 * 1000 * 1024);
    }

    public DefaultUploadFileService(String accessHost, int maxFileSizeBytes) {
        super(accessHost);
        this.maxFileSizeBytes = maxFileSizeBytes;
    }

    @Override
    public FileResponse validateFile(MultipartFile file) {
        if (maxFileSizeBytes > 0 && file.getSize() > maxFileSizeBytes) {
            return buildResponse("The file size larger than " + maxFileSizeBytes);
        }
        return buildResponse(true, null, null, null);
    }


    @Override
    public String generateFileName(String originalFilename) {
        return FileUtil.generateUUIDFileName(originalFilename);
    }


    public void setMaxFileSizeBytes(int maxFileSizeBytes) {
        this.maxFileSizeBytes = maxFileSizeBytes;
    }
}
