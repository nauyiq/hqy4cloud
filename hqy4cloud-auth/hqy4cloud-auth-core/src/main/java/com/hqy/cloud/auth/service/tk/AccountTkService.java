package com.hqy.cloud.auth.service.tk;

import com.hqy.cloud.account.dto.AccountInfoDTO;
import com.hqy.cloud.auth.entity.Account;
import com.hqy.cloud.common.result.PageResult;
import com.hqy.cloud.auth.base.vo.AccountInfoVO;
import com.hqy.cloud.db.tk.BaseTkService;

import java.util.List;

/**
 * @author qiyuan.hong
 * @date 2022-03-10 21:17
 */
public interface AccountTkService extends BaseTkService<Account, Long> {


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
     * 查找用户信息
     * @param ids 用户id 列表
     * @return    AccountInfoDTO Set.
     */
    List<AccountInfoDTO> getAccountInfos(List<Long> ids);

    /**
     * 分页查询查询用户列表
     * @param username     用户名模糊查询
     * @param nickname     角色名模糊查询
     * @param maxRoleLevel 用户最大角色级别
     * @param current      当前页
     * @param size         每页多少行
     * @return             PageResult for AccountInfoDTO.
     */
    PageResult<AccountInfoVO> getPageAccountInfos(String username, String nickname, Integer maxRoleLevel, Integer current, Integer size);

}
