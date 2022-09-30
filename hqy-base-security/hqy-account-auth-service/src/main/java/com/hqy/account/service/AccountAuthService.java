package com.hqy.account.service;

import com.hqy.account.dto.AccountInfoDTO;

/**
 * Account Auth Service Crud.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/9/27 15:43
 */
public interface AccountAuthService {


    /**
     * get account information,
     * @param id account id.
     * @return   AccountInfoDTO.
     */
    AccountInfoDTO getAccountInfo(Long id);


    /**
     * simple table crud for t_account.
     * @return AccountTkService.
     */
    AccountTkService getAccountTkService();

    /**
     * simple table crud for t_account_profile.
     * @return AccountProfileTkService.
     */
    AccountProfileTkService getAccountProfileTkService();

    /**
     * simple table crud for t_account_oauth_client.
     * @return AccountOauthClientTkService.
     */
    AccountOauthClientTkService getAccountOauthClientTkService();

}
