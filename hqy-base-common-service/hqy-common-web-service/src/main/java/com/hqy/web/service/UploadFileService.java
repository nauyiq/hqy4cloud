package com.hqy.web.service;

import com.hqy.base.common.base.lang.exception.UploadFileException;
import com.hqy.foundation.common.FileResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * UploadFileService.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/9/29 10:22
 */
public interface UploadFileService {

    String BASE_FOLDER = "/files";
    String DEFAULT_FOLDER =  BASE_FOLDER + "/common";
    String AVATAR_FOLDER = BASE_FOLDER + "/avatar";


    /**
     * upload user avatar file.
     * @param file                  avatar file.
     * @throws UploadFileException  e.
     * @return                      file response.
     */
    FileResponse uploadAvatar(final MultipartFile file) throws UploadFileException;

    /**
     * upload image file.
     * @param folderPath  folder
     * @param file        file
     * @return            fileResponse.
     * @throws UploadFileException
     */
    FileResponse uploadImgFile(String folderPath, final MultipartFile file) throws UploadFileException;

    /**
     * upload file to folderPath.
     * @param folderPath            folder
     * @param file                  file.
     * @throws UploadFileException  e
     * @return                      fileResponse.
     */
    FileResponse uploadFile(String folderPath, final MultipartFile file) throws UploadFileException;


    /**
     * add support image file type.
     * @param fileType image file type.
     */
    void addUploadSupportImgFileType(String fileType);

    /**
     * add support file type.
     * @param fileType file type.
     */
    void addUploadSupportFileType(String fileType);


}
