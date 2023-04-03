package com.hqy.cloud.auth.base.dto;

import com.hqy.cloud.util.ValidationUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/12/13 15:44
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    private Long id;
    private String username;
    private String nickname;
    private String email;
    private String phone;
    private String password;
    private String avatar;
    private Boolean status;
    private List<String> role;

    public boolean checkAddUser() {
        if (StringUtils.isAnyBlank(username, email, password)) {
            return false;
        }

        if (CollectionUtils.isEmpty(role)) {
            return false;
        }

        if (!ValidationUtil.validateEmail(email)) {
            return false;
        }

        return true;
    }

    public boolean checkUpdateUser() {
        if (this.id == null) {
            return false;
        }

        if (StringUtils.isAnyBlank(password)) {
            return false;
        }

        return true;
    }


}
