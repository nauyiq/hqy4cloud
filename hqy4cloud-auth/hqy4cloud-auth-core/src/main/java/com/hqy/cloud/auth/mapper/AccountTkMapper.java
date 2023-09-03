package com.hqy.cloud.auth.mapper;

import com.hqy.cloud.account.dto.AccountInfoDTO;
import com.hqy.cloud.auth.entity.Account;
import com.hqy.cloud.db.tk.BaseTkMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author qiyuan.hong
 * @date 2022-03-10 21:42
 */
@Repository
public interface AccountTkMapper extends BaseTkMapper<Account, Long> {


    /**
     * 根据用户名或者邮箱查询账号信息
     * @param usernameOrEmail 用户名或邮箱
     * @return                Account
     */
    Account queryAccountByUsernameOrEmail(@Param("usernameOrEmail") String usernameOrEmail);

    /**
     * 根据id查找用户信息
     * @param id id
     * @return   AccountInfoDTO.
     */
    AccountInfoDTO getAccountInfo(@Param("id") Long id);

    /**
     * return account info by username or email.
     * @param usernameOrEmail username or email.
     * @return               {@link AccountInfoDTO}
     */
    AccountInfoDTO getAccountInfoByUsernameOrEmail(@Param("usernameOrEmail")String usernameOrEmail);

    /**
     * 根据id集合查找用户信息
     * @param ids idjihe
     * @return    AccountInfoDTO Set.
     */
    List<AccountInfoDTO> getAccountInfos(@Param("ids") List<Long> ids);

    /**
     * 分页查询查询用户列表
     * @param username     用户名
     * @param nickname     昵称
     * @param maxRoleLevel 用户最大角色级别
     * @return             AccountInfoDTO.
     */
    List<AccountInfoDTO> getPageAccountInfos(@Param("username")String username, @Param("nickname")String nickname, @Param("maxLevel") Integer maxRoleLevel);


}
