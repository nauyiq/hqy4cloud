package com.hqy.web.service.support;

import com.hqy.cloud.common.base.lang.StringConstants;
import com.hqy.cloud.common.base.lang.exception.UploadFileException;
import com.hqy.cloud.common.result.CommonResultCode;
import com.hqy.foundation.common.FileResponse;
import com.hqy.cloud.util.CommonDateUtil;
import com.hqy.cloud.util.config.ConfigurationContext;
import com.hqy.cloud.util.file.FileUtil;
import com.hqy.cloud.util.file.FileValidateContext;
import com.hqy.web.service.UploadFileService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 * AbstractUploadFileService.
 * @see com.hqy.web.service.UploadFileService
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/9/29 11:13
 */
@Slf4j
public abstract class AbstractUploadFileService implements UploadFileService {

    /**
     * access file host.
     */
    private final String accessHost;

    /**
     * default file folder.
     */
    private final String defaultFolder;

    public AbstractUploadFileService(String accessHost) {
        this(accessHost, DEFAULT_FOLDER);
    }

    public AbstractUploadFileService(String accessHost, String defaultFolder) {
        this.accessHost = accessHost;
        this.defaultFolder = defaultFolder;
    }


    @Override
    public FileResponse uploadAvatar(MultipartFile file) throws UploadFileException {
        return uploadImgFile(AVATAR_FOLDER, file);
    }

    @Override
    public FileResponse uploadImgFile(String folderPath, MultipartFile file) throws UploadFileException {
        if (StringUtils.isBlank(folderPath)) {
            return buildResponse("Upload image file folderPath should not be null.");
        } else if (!folderPath.startsWith(StringConstants.Symbol.INCLINED_ROD)) {
            folderPath = StringConstants.Symbol.INCLINED_ROD + folderPath;
        }

        String originalFilename = file.getOriginalFilename();
        //checking file type.
        boolean validate = validateFileType(originalFilename, true);
        if (!validate) {
            return buildResponse("No support image file type.");
        }

        return validateFileAndWriteFile(originalFilename, folderPath, file);
    }

    @Override
    public FileResponse uploadFile(String folderPath, MultipartFile file) throws UploadFileException {
        if (StringUtils.isBlank(folderPath)) {
            folderPath = defaultFolder;
        } else if (!folderPath.startsWith(StringConstants.Symbol.INCLINED_ROD)) {
            folderPath = StringConstants.Symbol.INCLINED_ROD + folderPath;
        }

        String originalFilename = file.getOriginalFilename();
        //checking file type.
        boolean validate = validateFileType(originalFilename, false);
        if (!validate) {
            return buildResponse("No support file type.");
        }

        return validateFileAndWriteFile(originalFilename, folderPath, file);
    }

    @Override
    public void addUploadSupportImgFileType(String fileType) {
        if (StringUtils.isBlank(fileType)) {
            return;
        }
        if (!fileType.startsWith(StringConstants.Symbol.POINT)) {
            fileType = StringConstants.Symbol.POINT + fileType.trim();
        }
        FileValidateContext.SUPPORT_IMAGE_FILE_TYPES.add(fileType);
        log.info("Service add new support image file type, invalid after Service restart, image fileType: {}.", fileType);
    }

    @Override
    public void addUploadSupportFileType(String fileType) {
        if (StringUtils.isBlank(fileType)) {
            return;
        }
        if (!fileType.startsWith(StringConstants.Symbol.POINT)) {
            fileType = StringConstants.Symbol.POINT + fileType.trim();
        }
        FileValidateContext.SUPPORT_COMMON_FILE_TYPES.add(fileType);
        log.info("Service add new support file type, invalid after Service restart, fileType: {}.", fileType);
    }

    protected FileResponse validateFileAndWriteFile(String originalFilename, String folderPath, MultipartFile file) throws UploadFileException {
        //abstract validate file.
        FileResponse response = validateFile(file);
        if (!response.result()) {
            return response;
        }
        //write file.
        String baseFileName = generateFileName(originalFilename);
        String relativeFilePath = generateRelativeFilePath(folderPath, baseFileName);
        String uploadFilePath = ConfigurationContext.getConfigPath() + relativeFilePath;

        FileUtil.writeToFile(file, uploadFilePath);

        return buildResponse(relativeFilePath, accessHost + relativeFilePath);
    }



    protected boolean validateFileType(String filename, boolean imageType) {
        if (imageType) {
            return FileUtil.validateImgFileType(filename);
        } else {
            return FileUtil.validateFileType(filename);
        }
    }

    protected String generateRelativeFilePath(String folder, String baseFileName) {
        return folder +
                StringConstants.Symbol.INCLINED_ROD +
                CommonDateUtil.today() +
                StringConstants.Symbol.INCLINED_ROD +
                baseFileName;
    }

    /**
     * validate File.
     * @param file file.
     * @return     validate result.
     */
    public abstract FileResponse validateFile(MultipartFile file);

    /**
     * generate file name.
     * @param originalFilename originalFilename.
     * @return new file name.
     */
    public abstract String generateFileName(String originalFilename);


    protected FileResponse buildResponse(String message) {
        return buildResponse(false, message, null, null);
    }

    protected FileResponse buildResponse(String relativePath, String path) {
        return buildResponse(true, CommonResultCode.SUCCESS.message, relativePath, path);
    }

    protected FileResponse buildResponse(boolean result, String message, String relativePath, String path) {
        return new FileResponse() {
            @Override
            public boolean result() {
                return result;
            }

            @Override
            public String message() {
                return message;
            }

            @Override
            public String path() {
                return path;
            }

            @Override
            public String relativePath() {
                return relativePath;
            }
        };
    }




}
