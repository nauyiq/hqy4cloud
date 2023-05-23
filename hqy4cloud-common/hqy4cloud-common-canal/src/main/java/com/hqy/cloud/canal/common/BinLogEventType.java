package com.hqy.cloud.canal.common;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/5/19 10:09
 */
public enum BinLogEventType {

    /**
     * 查询
     */
    QUERY("QUERY", "查询"),

    /**
     * 插入
     */
    INSERT("INSERT", "新增"),

    /**
     * 更新
     */
    UPDATE("UPDATE", "更新"),

    /**
     * 删除
     */
    DELETE("DELETE", "删除"),

    /**
     * 列修改操作
     */
    ALTER("ALTER", "列修改操作"),

    /**
     * 表创建
     */
    CREATE("CREATE", "表创建"),

    /**
     * 未知
     */
    UNKNOWN("UNKNOWN", "未知"),

    ;

    BinLogEventType(String type, String description) {
        this.type = type;
        this.description = description;
    }

    private final String type;
    private final String description;


    public static BinLogEventType fromType(String type) {
        for (BinLogEventType binLogType : BinLogEventType.values()) {
            if (binLogType.getType().equals(type)) {
                return binLogType;
            }
        }
        return BinLogEventType.UNKNOWN;
    }

    public String getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }
}
