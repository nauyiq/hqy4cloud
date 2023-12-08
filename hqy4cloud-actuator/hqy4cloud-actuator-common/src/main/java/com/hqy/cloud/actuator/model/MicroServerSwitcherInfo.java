package com.hqy.cloud.actuator.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hqy.cloud.actuator.core.GradeSwitcherListener;
import com.hqy.cloud.common.swticher.AbstractSwitcher;
import com.hqy.cloud.util.AssertUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * SwitcherInfo
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/11/20 9:30
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MicroServerSwitcherInfo implements Serializable, Comparable<MicroServerSwitcherInfo> {

    private Integer id;
    private String name;
    private Object status;
    private AbstractSwitcher.S_TYPE type;
    @JsonIgnore
    private GradeSwitcherListener listener;


    public MicroServerSwitcherInfo(Integer id, Object status) {
        this.id = id;
        this.status = status;
    }

    public MicroServerSwitcherInfo(AbstractSwitcher switcher) {
        AssertUtil.notNull(switcher, "Switcher should not be null.");
        this.id = switcher.getId();
        this.name = switcher.getName();
        this.status = switcher.getStatus();
        this.type = switcher.getType();
    }

    public MicroServerSwitcherInfo(AbstractSwitcher switcher, GradeSwitcherListener listener) {
        this(switcher);
        this.listener = listener;
    }

    @JsonIgnore
    public Boolean getBooleanStatus() {
        if (this.type == AbstractSwitcher.S_TYPE.BOOLEAN) {
            return (Boolean) this.status;
        }
        return false;
    }


    @Override
    public int compareTo(MicroServerSwitcherInfo o) {
        if (o != null) {
            return this.id - o.getId();
        }
        return 0;
    }
}
