package com.hqy.cloud.auth.service.tansactional;

import com.hqy.cloud.auth.base.dto.UserDTO;
import com.hqy.cloud.auth.account.entity.Account;
import com.hqy.cloud.auth.account.entity.Role;
import io.seata.rm.tcc.api.BusinessActionContext;

import java.util.List;

/**
 * 注册账号
 * 基于seata的tcc事务， tc
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/3/19
 */
public interface TccRegistryAccountService {

    /**
     * try阶段，事务资源的锁定
     * @param account  用户实体
     * @param user     注册的用户信息
     * @param roles    该用户拥有的角色列表
     * @return         是否成功
     */
    boolean register( Account account,
                      UserDTO user,
                      List<Role> roles);

    /**
     * 提交阶段，
     * @param actionContext 上下文
     * @return              是否成功
     */
    boolean commit(BusinessActionContext actionContext);

    /**
     * 回滚阶段
     * @param actionContext 上下文
     * @return              是否成功
     */
    boolean rollback(BusinessActionContext actionContext);

}
