package com.hqy.cloud.common.dto;

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
 * @date 2022/12/16 13:43
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleMenuDTO {

    private String menuIds;
    @NotNull(message = "角色id不能为空.")
    private Integer roleId;

    public List<Integer> pauseMenuIds() {
        if (StringUtils.isBlank(this.menuIds)) {
            return Collections.emptyList();
        }
        String[] stringIds = org.springframework.util.StringUtils.tokenizeToStringArray(this.menuIds, COMMA);
        return Arrays.stream(stringIds).map(Integer::valueOf).collect(Collectors.toList());
    }



}
