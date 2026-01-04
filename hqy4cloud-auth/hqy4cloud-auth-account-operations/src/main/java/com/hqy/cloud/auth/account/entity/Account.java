package com.hqy.cloud.auth.account.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hqy.cloud.account.constants.AccountStatus;
import com.hqy.cloud.auth.common.UserRole;
import com.hqy.cloud.db.entity.CommonEntity;
import com.hqy.cloud.db.handler.AesEncryptTypeHandler;
import com.hqy.cloud.sharding.id.DistributedIdGen;
import com.hqy.cloud.sharding.id.WorkerIdHolder;
import lombok.*;

/**
 * 账户表 t_account
 * @author qiyuan.hong
 * @date 2022-03-10
 */
@Data
@ToString
@TableName("account")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Account extends CommonEntity {

    /**
     * 商户客户端ID
     */
    private String clientId;

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
     * 是否实名
     */
    private Boolean certification;

    /**
     * 用户角色
     */
    private UserRole role;

    /**
     * 状态
     */
    private AccountStatus status;


    public Account(Long id, String clientId, String username, String password, String email, String phone, UserRole role) {
        setId(id);
        this.username = username;
        this.clientId = clientId;
        this.password = password;
        this.email = email;
        this.role = role;
        this.phone = phone;
        this.status = AccountStatus.ACTIVE;
    }

    public static Account register(String clientId, String username, String password, String email, String phone, UserRole userRole) {
        long accountId = DistributedIdGen.getSnowflakeId(WorkerIdHolder.workerId);
        if (userRole == null) {
            userRole = UserRole.CUSTOMER;
        }
        return new Account(accountId, clientId, username, password, email, phone, userRole);
    }

    public void auth(String rearName, String idCard) {
        setCertification(true);
        setRealName(rearName);
        setIdCard(idCard);
    }
}
