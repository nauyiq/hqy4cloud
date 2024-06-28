package com.hqy.cloud.auth.account.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hqy.cloud.auth.base.dto.AccountInfoDTO;
import com.hqy.cloud.auth.base.vo.AccountInfoVO;
import com.hqy.cloud.auth.account.entity.Account;
import com.hqy.cloud.common.result.PageResult;

import java.util.List;

/**
 * @author qiyuan.hong
 * @date 2022-03-10
 */
public interface AccountService extends IService<Account> {

    /**
     * 根据邮箱或用户名或手机获取账号id
     * @param value 邮箱或用户名
     * @return      用户id
     */
    Long getAccountIdByUsernameOrEmail(String value);

    /**
     * 根据用户名或者邮箱查询账号信息
     * @param usernameOrEmail 用户名或邮箱
     * @return                Account
     */
    Account queryAccountByUsernameOrEmail(String usernameOrEmail);

    /**
     * 查找用户信息
     * @param id 用户id
     * @return   AccountInfoDTO.
     */
    AccountInfoDTO getAccountInfo(Long id);

    /**
     * return account info by username or email.
     * @param usernameOrEmail username or email.
     * @return               {@link AccountInfoDTO}
     */
    AccountInfoDTO getAccountInfoByUsernameOrEmail(String usernameOrEmail);

    /**
     * 查找用户信息
     * @param ids 用户id 列表
     * @return    AccountInfoDTO Set.
     */
    List<AccountInfoDTO> getAccountInfos(List<Long> ids);

    /**
     * get account information by name
     * @param name username or nickname
     * @return    {@link AccountInfoDTO}
     */
    List<AccountInfoDTO> getAccountInfosByName(String name);

    /**
     * 分页查询查询用户列表
     * @param username     用户名模糊查询
     * @param nickname     角色名模糊查询
     * @param current      当前页
     * @param size         每页多少行
     * @return             PageResult for AccountInfoDTO.
     */
    PageResult<AccountInfoVO> getPageAccountInfos(String username, String nickname, Integer current, Integer size);



}
