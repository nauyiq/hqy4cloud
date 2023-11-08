package com.hqy.cloud.auth.mapper;

import com.hqy.cloud.auth.entity.SysOauthClient;
import com.hqy.cloud.db.tk.PrimaryLessTkMapper;
import org.springframework.stereotype.Repository;

/**
 * @author qiyuan.hong
 * @date 2022-03-16 14:51
 */
@Repository
public interface SysOauthClientTkMapper extends PrimaryLessTkMapper<SysOauthClient> {


}
