package com.hqy.cloud.file.common;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/5/25 11:27
 */
public interface FileConstants {

    String REGION = "region";
    String TENCENT_DEFAULT_REGION = "ap-guangzhou";
    String TENCENT_BUCKET_ALREADY_EXIST = "BucketAlreadyExists";


    String BASE_FOLDER = "/files";
    String DEFAULT_FOLDER =  BASE_FOLDER + "/common";
    String AVATAR_FOLDER = BASE_FOLDER + "/avatar";
}
