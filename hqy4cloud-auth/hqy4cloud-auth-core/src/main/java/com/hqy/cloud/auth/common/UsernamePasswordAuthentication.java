package com.hqy.cloud.auth.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/12/7
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsernamePasswordAuthentication {

    private String username;
    private String password;

}
