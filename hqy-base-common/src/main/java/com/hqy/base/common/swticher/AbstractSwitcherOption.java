package com.hqy.base.common.swticher;

/**
 * @author qy
 * @project: hqy-parent-all
 * @create 2021-07-27 17:16
 */
public abstract class AbstractSwitcherOption {

    private Object value;
    private String description;

    @Override
    public String toString() {
        return "AbstractSwitcherOption [value=" + value + ", description=" + description + "]";
    }


    public AbstractSwitcherOption(Object value, String description) {
        super();
        this.value = value;
        this.description = description;
    }
    /**
     * @return the value
     */
    public Object getValue() {
        return value;
    }
    /**
     * @param value the value to set
     */
    public void setValue(Object value) {
        this.value = value;
    }
    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }
    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }


}
