package com.hqy.cloud.auth.base.dto;

import lombok.*;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/10/8 11:09
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class AccountDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = -492977226753159315L;

    private Long id;
    private String username;
    private String email;
    private String phone;
    private String password;
    private String roles;
    private Boolean status;
    private Long created;

}
