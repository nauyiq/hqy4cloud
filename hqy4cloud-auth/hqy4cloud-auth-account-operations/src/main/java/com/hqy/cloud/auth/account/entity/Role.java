package com.hqy.cloud.auth.account.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.google.common.base.Objects;
import com.hqy.cloud.db.mybatisplus.BaseEntity;
import lombok.*;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serial;
import java.util.Date;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/3/11 11:30
 */
@Getter
@Setter
@TableName("t_role")
@AllArgsConstructor
@NoArgsConstructor
public class Role extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 4205584555574408604L;

    @TableId(type = IdType.AUTO)
    private Integer id;
    private String name;
    private Integer level;
    private String note;
    private Boolean status;
    private Boolean deleted = false;

    public Role(String name) {
        this.name = name;
    }

    public Role(String name, Integer level, String note) {
        super(new Date());
        this.name = name;
        this.level = level;
        this.note = note;
        this.status = true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Role role = (Role) o;
        return Objects.equal(name, role.name) && Objects.equal(status, role.status);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(super.hashCode(), name, status);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("name", name)
                .append("level", level)
                .toString();
    }
}
