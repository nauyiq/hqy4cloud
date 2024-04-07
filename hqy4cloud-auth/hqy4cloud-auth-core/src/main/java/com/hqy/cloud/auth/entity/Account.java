package com.hqy.cloud.auth.entity;

import cn.hutool.core.util.StrUtil;
import com.hqy.cloud.auth.base.dto.UserDTO;
import com.hqy.cloud.common.base.project.MicroServiceConstants;
import com.hqy.cloud.db.tk.model.BaseEntity;
import com.hqy.cloud.foundation.id.DistributedIdGen;
import com.hqy.cloud.util.ValidationUtil;
import lombok.*;

import javax.persistence.Table;
import java.io.Serial;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.hqy.cloud.common.base.lang.StringConstants.Symbol.COMMA;

/**
 * 账户表 t_account
 * @author qiyuan.hong
 * @date 2022-03-10
 */
@Data
@ToString
@Table(name = "t_account")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Account extends BaseEntity<Long> {

    @Serial
    private static final long serialVersionUID = -7814298685660847656L;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 用户角色
     */
    private String roles;

    /**
     * 状态
     */
    private Boolean status;

    /**
     * 是否删除
     */
    private Boolean deleted = false;


    public Account(String usernameOrEmail) {
        if (ValidationUtil.validateEmail(usernameOrEmail)) {
            this.email = usernameOrEmail;
        } else {
            this.username = usernameOrEmail;
        }
    }

    public Account(Long id, String username, String password) {
        this(id, username, password, null, null, null);
    }

    public Account(String username, String password, String email, String roles) {
        this(null, username, password, email, roles, null);
    }

    public Account(Long id, String username, String password, String email, String roles, String phone) {
        super(id, new Date());
        this.username = username;
        this.password = password;
        this.email = email;
        this.roles = roles;
        this.phone = phone;
    }

    public static Account of(UserDTO userDTO, List<Role> roles) {
        List<String> roleNames = roles.stream().map(Role::getName).collect(Collectors.toList());
        String role = StrUtil.join(COMMA, roleNames);
        Account account = new Account(DistributedIdGen.getSnowflakeId(MicroServiceConstants.ACCOUNT_SERVICE), userDTO.getUsername(), userDTO.getPassword(), userDTO.getEmail(), role, userDTO.getPhone());
        if (Objects.nonNull(userDTO.getStatus())) {
            account.setStatus(userDTO.getStatus());
        }
        return account;
    }

}
