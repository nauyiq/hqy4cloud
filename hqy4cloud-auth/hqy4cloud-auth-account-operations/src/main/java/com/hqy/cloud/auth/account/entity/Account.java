package com.hqy.cloud.auth.account.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.github.houbb.sensitive.annotation.strategy.SensitiveStrategyEmail;
import com.github.houbb.sensitive.annotation.strategy.SensitiveStrategyPhone;
import com.hqy.cloud.auth.base.AccountConstants;
import com.hqy.cloud.auth.common.UserRole;
import com.hqy.cloud.db.handler.AesEncryptTypeHandler;
import com.hqy.cloud.db.mybatisplus.BaseEntity;
import com.hqy.cloud.sharding.id.DistributedIdGen;
import com.hqy.cloud.sharding.id.WorkerIdHolder;
import lombok.*;
import org.apache.commons.collections4.CollectionUtils;

import java.io.Serial;
import java.util.Date;
import java.util.List;

import static com.hqy.cloud.common.base.lang.StringConstants.Symbol.COMMA;

/**
 * 账户表 t_account
 * @author qiyuan.hong
 * @date 2022-03-10
 */
@Data
@ToString
@TableName("t_account")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Account extends BaseEntity {
    @Serial
    private static final long serialVersionUID = -7814298685660847656L;

    /**
     * id
     */
    @TableId
    private Long id;

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
    @SensitiveStrategyEmail
    @TableField(typeHandler = AesEncryptTypeHandler.class)
    private String email;

    /**
     * 手机号
     */
    @SensitiveStrategyPhone
    @TableField(typeHandler = AesEncryptTypeHandler.class)
    private String phone;

    /**
     * 真实姓名
     */
    @TableField(typeHandler = AesEncryptTypeHandler.class)
    private String realName;

    /**
     * 身份证
     */
    @TableField(typeHandler = AesEncryptTypeHandler.class)
    private String idCard;

    /**
     * 用户角色
     */
    private UserRole role;

    /**
     * 拥有的权限
     */
    private String authorities;

    /**
     * 状态
     */
    private Boolean status;

    /**
     * 是否删除
     */
    private Boolean deleted = false;

    public Account(Long id, String username, String password, String email, String phone, UserRole role, List<String> authorities) {
        super(new Date());
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
        this.phone = phone;
        this.status = true;
        // 将用户ROLE也加入authorities
        if (CollectionUtils.isNotEmpty(authorities)) {
            authorities.add(this.role.name());
        } else {
            authorities = List.of(this.role.name());
        }
        this.authorities = String.join(COMMA, authorities);
    }

    public static Account register(String username, String password, String email, String phone, UserRole userRole, List<String> authorities) {
        // 生成分布式用户ID
        long accountId = DistributedIdGen.getSnowflakeId(WorkerIdHolder.workerId);
        if (userRole == null) {
            userRole = UserRole.CUSTOMER;
        }
        if (CollectionUtils.isEmpty(authorities)) {
            authorities = List.of(userRole.name());
        } else {
            authorities.add(userRole.name());
        }
        return new Account(accountId, username, password, email, phone, userRole, authorities);
    }

}
