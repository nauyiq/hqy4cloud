package com.hqy.admin.service.request;

import com.hqy.cloud.common.bind.DataResponse;
import com.hqy.cloud.common.bind.MessageResponse;
import com.hqy.cloud.common.dto.BlackWhitelistDTO;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/1/10 16:32
 */
public interface AdminSystemSettingRequestService {

    /**
     * 查询白名单
     * @return DataResponse.
     */
    DataResponse queryWhitelist();

    /**
     * 添加白名单
     * @param whiteListDTOBlack {@link BlackWhitelistDTO}
     * @return             MessageResponse.
     */
    MessageResponse addWhitelist(BlackWhitelistDTO whiteListDTOBlack);



    /**
     * 删除白名单
     * @param type  类型
     * @param value 值
     * @return      MessageResponse.
     */
    MessageResponse deleteWhitelist(String type, String value);

    /**
     * 查询黑名单
     * @return DataResponse.
     */
    DataResponse queryBlacklist();

    /**
     * 新增黑名单
     * @param blackWhitelistDTO {@link BlackWhitelistDTO}
     * @return                  MessageResponse.
     */
    MessageResponse addBlacklist(BlackWhitelistDTO blackWhitelistDTO);

    /**
     * 删除黑名单
     * @param type  类型
     * @param value 值
     * @return      MessageResponse.
     */
    MessageResponse deleteBlacklist(String type, String value);
}
