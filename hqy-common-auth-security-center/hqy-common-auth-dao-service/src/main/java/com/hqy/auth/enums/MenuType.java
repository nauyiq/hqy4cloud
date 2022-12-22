package com.hqy.auth.enums;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/12/12 10:51
 */
@AllArgsConstructor
public enum MenuType {

    /**
     * 菜单
     */
    MENU(0),

    /**
     * 按钮
     */
    BUTTON(1),

    ;

    public final int type;


    public static MenuType findMenuType(int type) {
        for (MenuType value : values()) {
            if (value.type == type) {
                return value;
            }
        }
        return null;
    }


}
