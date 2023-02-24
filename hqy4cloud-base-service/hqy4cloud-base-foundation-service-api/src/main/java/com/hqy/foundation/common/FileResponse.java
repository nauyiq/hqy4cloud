package com.hqy.foundation.common;

/**
 * FileResponse.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/9/29 10:23
 */
public interface FileResponse {

    /**
     * is upload file success?
     * @return result.
     */
    boolean result();

    /**
     * upload file message.
     * @return message.
     */
    String message();


    /**
     * return file full path.
     * @return file full path.
     */
    String path();

    /**
     * return file relative path.
     * @return file relative path
     */
    String relativePath();









}
