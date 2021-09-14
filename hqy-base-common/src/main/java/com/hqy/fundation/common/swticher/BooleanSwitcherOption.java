package com.hqy.fundation.common.swticher;

/**
 * @author qy
 * @project: hqy-parent-all
 * @create 2021-07-27 17:16
 */
public final class BooleanSwitcherOption extends AbstractSwitcherOption {

    public BooleanSwitcherOption(Object value, String description) {
        super(value, description);
    }

    public static final BooleanSwitcherOption TRUE = new BooleanSwitcherOption(Boolean.TRUE, "开");

    public static final BooleanSwitcherOption FALSE = new BooleanSwitcherOption(Boolean.FALSE, "关");

}
