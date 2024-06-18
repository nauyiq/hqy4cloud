package com.hqy.cloud.common.swticher;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author qy
 * @date 2021-07-27 16:52
 */
public abstract class IdSwitcher implements Comparable<Object>, Serializable {
    @Serial
    private static final long serialVersionUID = -8928471116963388732L;

    /**
     * 开关id，需要在同一个类中唯一不重复
     */
    private int id;

    /**
     * 开关名称
     */
    private String name;

    private static final Map<String, IdSwitcher> families = new HashMap<>();

    protected static Map<String, List<IdSwitcher>> enumFamilies = new HashMap<>();

    public IdSwitcher() {

    }

    protected IdSwitcher(String name, int id) {
        this.name = name;
        this.id = id;
        families.put(getFamilyName(id), this);
        putEnumFamilies();
    }


    private void putEnumFamilies() {
        @SuppressWarnings("unchecked")
        Class<IdSwitcher> clazz = (Class<IdSwitcher>) getClass();
        List<IdSwitcher> list;
        if (!enumFamilies.containsKey(clazz.getName())) {
            list = new ArrayList<>();
            enumFamilies.put(clazz.getName(), list);
        } else {
            list = enumFamilies.get(clazz.getName());
        }
        list.add(this);
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public IdSwitcher[] values() {
        @SuppressWarnings("unchecked")
        Class<IdSwitcher> clazz = (Class<IdSwitcher>) getClass();
        List<IdSwitcher> list = enumFamilies.get(clazz.getName());
        if (list == null || list.size() == 0) {
            return new IdSwitcher[0];
        }
        return list.toArray(new IdSwitcher[0]);
    }


    public IdSwitcher getEnum(int id) {
        return families.get(getFamilyName(id));
    }

    protected String getFamilyName(int id) {
        return this.getClass().getName() + "$" + id;
    }

    @Override
    public String toString() {
        return "name:" + getName() + ",:" + getId();
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
        result = prime * result + getId();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        IdSwitcher other = (IdSwitcher) obj;
        if (getName() == null) {
            if (other.getName() != null)
                return false;
        } else if (!getName().equals(other.getName()))
            return false;
        return getId().equals(other.getId());
    }

    @Override
    public int compareTo(Object o) {
        if (o instanceof IdSwitcher target) {
            return this.getId() - target.getId();
        } else {
            throw new IllegalArgumentException("parameter not recognized...");
        }
    }


    public static String getReplaceStr(IdSwitcher[] array) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < array.length; i++) {
            IdSwitcher ise = array[i];
            sb.append(ise.getName()).append("_").append(ise.getId());
            if (i + 1 != array.length) {
                sb.append(",");
            }
        }
        return sb.toString();
    }
}
