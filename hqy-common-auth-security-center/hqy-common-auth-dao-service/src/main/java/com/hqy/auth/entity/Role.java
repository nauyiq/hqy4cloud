package com.hqy.auth.entity;

import com.google.common.base.Objects;
import com.hqy.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.Table;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/3/11 11:30
 */
@Data
@Table(name = "t_role")
@AllArgsConstructor
@NoArgsConstructor
public class Role extends BaseEntity<Integer> {

    private static final long serialVersionUID = 4205584555574408604L;

    private String name;

    private Integer level;

    private Boolean status;

    public Role(String name) {
        this.name = name;
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
