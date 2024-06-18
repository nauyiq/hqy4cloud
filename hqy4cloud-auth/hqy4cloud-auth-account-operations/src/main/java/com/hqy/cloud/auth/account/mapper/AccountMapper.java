package com.hqy.cloud.auth.account.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hqy.cloud.auth.base.dto.AccountInfoDTO;
import com.hqy.cloud.auth.account.entity.Account;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author qiyuan.hong
 * @date 2022-03-10 21:42
 */
@Mapper
public interface AccountMapper extends BaseMapper<Account> {

    /**
     * 获取账号id
     * @param value 邮箱、用户名、手机号码
     * @return      账号id
     */
    Long getAccountIdByUsernameOrEmail(@Param("value") String value);

    /**
     * 根据id获取账号信息
     * @param id 账号id
     * @return   账号实体
     */
    Account findById(@Param("id") Long id);

    /**
     * 根据用户名或者邮箱查询账号信息
     * @param uniqueIndex     唯一索引
     * @return                Account
     */
    Account queryAccountByUniqueIndex(@Param("value") String uniqueIndex);



    /**
     * 根据id查找用户信息
     * @param id id
     * @return   AccountInfoDTO.
     */
    AccountInfoDTO getAccountInfo(@Param("id") Long id);


    /**
     * 根据电话或者邮箱获取用户信息
     * @param phoneOrEmail 电话或邮箱
     * @return  用户信息
     */
    AccountInfoDTO getAccountInfoByPhoneOrEmail(@Param("value") String phoneOrEmail);

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
     * 模糊查询昵称和精确查询用户名
     * @param name 用户名或昵称
     * @return     {@link AccountInfoDTO}
     */
    List<AccountInfoDTO> getAccountProfilesByName(@Param("name") String name);

    /**
     * 分页查询查询用户列表
     * @param username     用户名
     * @param nickname     昵称
     * @return             AccountInfoDTO.
     */
    List<AccountInfoDTO> getPageAccountInfos(@Param("username")String username, @Param("nickname")String nickname);



}
