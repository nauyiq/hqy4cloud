package com.hqy.cloud.auth.base.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.hqy.cloud.common.base.lang.StringConstants.Symbol.COMMA;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/12/21 14:24
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleResourcesDTO {

    @NotNull(message = "资源id不能为空.")
    private Integer resourceId;
    private String roleIds;

    public List<Integer> pauseRoleIds() {
        if (StringUtils.isBlank(this.roleIds)) {
            return Collections.emptyList();
        }
        String[] stringIds = org.springframework.util.StringUtils.tokenizeToStringArray(this.roleIds, COMMA);
        return Arrays.stream(stringIds).map(Integer::valueOf).collect(Collectors.toList());
    }


}
