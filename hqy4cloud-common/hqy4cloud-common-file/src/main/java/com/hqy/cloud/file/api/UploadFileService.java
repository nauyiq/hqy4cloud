package com.hqy.cloud.file.api;

import com.hqy.cloud.common.base.lang.exception.UploadFileException;
import com.hqy.cloud.file.common.result.UploadResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * UploadFileService.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/9/29 10:22
 */
public interface UploadFileService {

    /**
     * upload user avatar file.
     * @param file                  avatar file.
     * @throws UploadFileException  e.
     * @return                      file response.
     */
    UploadResponse uploadAvatar(final MultipartFile file) throws UploadFileException;

    /**
     * upload image file.
     * @param folderPath  folder
     * @param file        file
     * @return            fileResponse.
     * @throws UploadFileException
     */
    UploadResponse uploadImgFile(String folderPath, final MultipartFile file) throws UploadFileException;

    /**
     * upload file to folderPath.
     * @param folderPath            folder
     * @param file                  file.
     * @throws UploadFileException  e
     * @return                      fileResponse.
     */
    UploadResponse uploadFile(String folderPath, final MultipartFile file) throws UploadFileException;

    /**
     * 几张图片生成一张图片, 类似wechat群聊头像
     * @param files   文件路径
     * @param folder  输出到的文件夹
     * @return        result
     * @throws UploadFileException e
     */
    UploadResponse generateFile(List<String> files, String folder) throws UploadFileException;


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
