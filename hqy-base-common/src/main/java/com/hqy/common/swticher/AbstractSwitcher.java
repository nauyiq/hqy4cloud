package com.hqy.common.swticher;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.beans.Transient;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * 升降级开关<br>
 *
 * @author qy
 * @project: hqy-parent-all
 * @create 2021-07-27 16:30
 */
public class AbstractSwitcher extends IdSwitcher {

    /**
     * ACTUATOR 开关类型
     */
    public enum S_TYPE {
        /**
         * 布尔型开关，
         */
        BOOLEAN,
        /**
         * 无符号短整形信道开关
         */
        UNSIGNED_SHORT,

        /**
         * 变长字符串开关
         */
        VARCHAR
    }

    /**
     *
     */
    private static final long serialVersionUID = 4279013225825284492L;


    public static final String ILL_MSG = "S_TYPE ERROR, SHOULD NOT CALL THIS METHOD!!";


    /**
     * 开关状态(boolean/unsigned short/String)
     */
    private Object status = false;

    /**
     * 开关类型枚举 (boolean/unsigned short/String)
     */
    private S_TYPE type = S_TYPE.BOOLEAN;

    /**
     * 选项列表，开关value的备选值
     */
    private List<AbstractSwitcherOption> choiceList;

    /**
     * 布尔型 Actuator开关构造器(默认)
     *
     * @param id
     * @param name
     * @param status
     */
    protected AbstractSwitcher(int id, String name, boolean status) {
        super(name, id);
        this.status = status;
        this.type = S_TYPE.BOOLEAN;
        this.choiceList = new ArrayList<>();
        this.choiceList.add(BooleanSwitcherOption.TRUE);
        this.choiceList.add(BooleanSwitcherOption.FALSE);
    }

    /**
     * 无符号短整型 Actuator开关构造器
     *
     * @param id
     * @param name
     * @param status
     */
    protected AbstractSwitcher(int id, String name, short status) {
        super(name, id);
        this.status = status;
        this.type = S_TYPE.UNSIGNED_SHORT;
        this.choiceList = new ArrayList<>();
    }

    /**
     * VARCHAR型 Actuator开关构造器
     *
     * @param id
     * @param name
     * @param status
     */
    protected AbstractSwitcher(int id, String name, String status) {
        super(name, id);
        this.status = status;
        this.type = S_TYPE.VARCHAR;
        this.choiceList = new ArrayList<AbstractSwitcherOption>();
    }


    @JsonIgnore
    @Transient
    public boolean getBooleanStatus() {
        if (this.type == S_TYPE.BOOLEAN) {
            return (boolean) status;
        }
        throw new IllegalAccessError(ILL_MSG);
    }

    @JsonIgnore
    @Transient
    public short getShortStatus() {
        if (this.type == S_TYPE.UNSIGNED_SHORT) {
            return (short) status;
        }
        throw new IllegalAccessError(ILL_MSG);
    }

    @JsonIgnore
    @Transient
    public String getVarcharStatus() {
        if (this.type == S_TYPE.VARCHAR) {
            return (String) status;
        }
        throw new IllegalAccessError(ILL_MSG);
    }


    /**
     * 开关是否是ON状态(仅仅适用于STYPE.BOOLEAN)
     *
     * @return
     */
    public boolean isOn() {
        if (this.type == S_TYPE.BOOLEAN) {
            return (boolean) status;
        }
        throw new IllegalAccessError(ILL_MSG);
    }

    /**
     * 开关是否是Off状态(仅仅适用于STYPE.BOOLEAN)
     *
     * @return
     */
    public boolean isOff() {
        if (this.type == S_TYPE.BOOLEAN) {
            return !((boolean) status);
        }
        throw new IllegalAccessError(ILL_MSG);
//		return status == false;
    }

    public Object getStatus() {
        return this.status;
    }

    public void setStatus(Object status) {
        this.status = status;
    }

    public static AbstractSwitcher[] allValues() {
        return allValues(null);
    }

    public static AbstractSwitcher[] allValues(Class<? extends AbstractSwitcher> clazz) {
        List<IdSwitcher> listTmp;
        if (clazz == null) {
            listTmp = new ArrayList<>();
            Collection<List<IdSwitcher>> tmp = enumFamilies.values();
            if (tmp.size() > 0) {
                Object[] arr = tmp.toArray();
                for (Object o : arr) {
                    if (o instanceof List) {
                        @SuppressWarnings("unchecked")
                        List<IdSwitcher> ll = (List<IdSwitcher>) o;
                        listTmp.addAll(ll);
                    }
                }
            }
        } else {
            listTmp = enumFamilies.get(clazz.getName());
            if (listTmp == null || listTmp.size() == 0) {
                listTmp = new ArrayList<>();
                //尝试反射取值
                try {
                    Field[] fields = clazz.getFields();
                    for (Field field : fields) {
                        if (Modifier.isStatic(field.getModifiers())) {
                            try {
                                Object object = field.get(null);
                                if (object instanceof AbstractSwitcher) {
                                    listTmp.add((AbstractSwitcher) object);
                                }
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    //obj = null;
                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    if (listTmp.size() > 0) {
                        enumFamilies.put(clazz.getName(), listTmp);
                    }
                }
            }
        }

        if (listTmp.size() == 0) {
            return new AbstractSwitcher[0];
        }

        Set<IdSwitcher> tmpSet = new HashSet<>(listTmp);

        AbstractSwitcher[] arr = new AbstractSwitcher[tmpSet.size()];
        int index = 0;
        for (IdSwitcher en : tmpSet) {
            arr[index] = (AbstractSwitcher) en;
            index++;
        }
        return arr;
    }

    public static AbstractSwitcher getSwitcherById(int id, Class<? extends AbstractSwitcher> clazz) {
        AbstractSwitcher[] arr = allValues(clazz);
        if (arr == null || arr.length == 0) {
            return null;
        }
        for (AbstractSwitcher xx : arr) {
            if (xx.getId() == id) {
                return xx;
            }
        }
        return null;
    }

    public String toString() {
        return new StringBuffer().append("[id=").append(this.getId())
                .append(",name=").append(this.getName())
                .append(",status=").append(status)
                .append("]").toString();
    }

    /**
     * (boolean/unsigned short/varchar)
     *
     * @return the stype
     */
    public S_TYPE getType() {
        return type;
    }

    /**
     * @param stype the stype to set  (boolean/unsigned short/varchar)
     */
    public void setType(S_TYPE stype) {
        this.type = stype;
    }

    /**
     * 开关value的备选值
     *
     * @return the choiceList
     */
    public List<AbstractSwitcherOption> getChoiceList() {
        return choiceList;
    }

    /**
     * 开关value的备选值
     *
     * @param choiceList the choiceList to set
     */
    public void setChoiceList(List<AbstractSwitcherOption> choiceList) {
        this.choiceList = choiceList;
    }

    public void addSwitcherOption(AbstractSwitcherOption option) {
        this.choiceList.add(option);
    }


}
