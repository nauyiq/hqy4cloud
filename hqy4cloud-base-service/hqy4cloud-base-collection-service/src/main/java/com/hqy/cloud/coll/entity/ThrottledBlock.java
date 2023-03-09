package com.hqy.cloud.coll.entity;

import com.hqy.cloud.tk.model.BaseEntity;
import com.hqy.cloud.coll.struct.ThrottledBlockStruct;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Table;
import java.util.Date;

/**
 * 节流封禁记录表 entity
 * @author qy
 * @date 2021-08-10 11:43
 */
@Data
@Table(name = "t_throttle_block_history")
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class ThrottledBlock extends BaseEntity<Long> {
    private static final long serialVersionUID = 7973842091244776229L;

    /**
     * 被什么方式节流的
     */
    private String throttleBy;

    /**
     * 请求的客户端ip
     */
    private String ip;

    /**
     * 请求url
     */
    private String url;

    /**
     * request json
     */
    private String accessJson ;

    /**
     * 封禁时间 单位s
     */
    private Integer blockedSeconds;

    /**
     * 所属环境
     */
    private String env;

    public ThrottledBlock(ThrottledBlockStruct struct) {
        super(new Date());
        this.throttleBy = struct.throttleBy;
        this.ip = struct.ip;
        this.url = struct.url;
        this.accessJson = struct.accessJson;
        this.blockedSeconds = struct.blockedSeconds;
        this.env = struct.env;
    }
}
