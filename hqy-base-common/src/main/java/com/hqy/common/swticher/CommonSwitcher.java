package com.hqy.common.swticher;

/**
 * @author qy
 * @project: hqy-parent-all
 * @create 2021-07-28 9:35
 */
public class CommonSwitcher extends AbstractSwitcher {

    protected CommonSwitcher(int id, String name, boolean status) {
        super(id, name, status);
    }




    /**
     * 节点-测试开关（仅仅用于开关测试，不要用于业务规则判定）
     */
    public static final CommonSwitcher JUST_4_TEST_DEBUG = new CommonSwitcher(250,"节点-DEBUG开关（默认关）",false);




}
