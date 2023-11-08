package com.hqy.cloud.auth.entity;

import com.google.common.base.Objects;
import com.hqy.cloud.db.tk.model.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.Table;
import java.io.Serial;
import java.util.Date;

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

    @Serial
    private static final long serialVersionUID = 4205584555574408604L;

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
