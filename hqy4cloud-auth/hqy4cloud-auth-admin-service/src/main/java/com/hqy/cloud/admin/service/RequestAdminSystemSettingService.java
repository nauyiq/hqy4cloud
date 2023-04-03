package com.hqy.cloud.admin.service;

import com.hqy.cloud.common.bind.DataResponse;
import com.hqy.cloud.common.bind.MessageResponse;
import com.hqy.cloud.auth.base.dto.BlackWhitelistDTO;
import com.hqy.cloud.common.bind.R;

import java.util.Set;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/1/10 16:32
 */
public interface RequestAdminSystemSettingService {

    /**
     * 查询白名单
     * @return R.
     */
    R<Set<BlackWhitelistDTO>> queryWhitelist();

    /**
     * 添加白名单
     * @param whiteListDTOBlack {@link BlackWhitelistDTO}
     * @return                  R.
     */
    R<Boolean> addWhitelist(BlackWhitelistDTO whiteListDTOBlack);

    /**
     * 删除白名单
     * @param type  类型
     * @param value 值
     * @return      R.
     */
    R<Boolean> deleteWhitelist(String type, String value);

    /**
     * 查询黑名单
     * @return R.
     */
    R<Set<BlackWhitelistDTO>> queryBlacklist();

    /**
     * 新增黑名单
     * @param blackWhitelistDTO {@link BlackWhitelistDTO}
     * @return                  R.
     */
    R<Boolean> addBlacklist(BlackWhitelistDTO blackWhitelistDTO);

    /**
     * 删除黑名单
     * @param type  类型
     * @param value 值
     * @return      MessageResponse.
     */
    R<Boolean> deleteBlacklist(String type, String value);
}
