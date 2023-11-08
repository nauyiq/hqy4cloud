package com.hqy.cloud.util;

import com.hqy.cloud.common.base.lang.StringConstants;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 反射工具类
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/4/21 15:19
 */
public class ReflectUtils {


    /**
     * 获取目标class的泛型class<t>
     * @param clazz 目标class
     * @param index 泛型下标
     * @param <T>   Class<T>
     * @return      Class<T>
     */
    @SuppressWarnings("unchecked")
    public static <T> Class<T> getTargetGenericClass(Class<?> clazz, int index) {
        ParameterizedType genericSuperclass = (ParameterizedType) clazz.getGenericSuperclass();
        Type[] actualTypeArguments = genericSuperclass.getActualTypeArguments();
        return (Class<T>) actualTypeArguments[index];
    }

    /**
     * 获取某个实现的接口的泛型class对象
     * @param clazz          需要查找class类
     * @param interfaceIndex 实现的接口下标
     * @param index          泛型下标
     * @param <T>            Class<T>
     * @return               Class<T>
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T> Class<T> getGenericInterfaceClass(Class clazz, int interfaceIndex, int index) {
        //获取到接口类型
        Type[] interfaces = clazz.getGenericInterfaces();
        Type type = interfaces[interfaceIndex];
        //如果没有实现ParameterizedType接口，即不支持泛型，直接返回Class
        if (!(type instanceof ParameterizedType)) {
            return clazz;
        } else {
            Type[] actualTypeArguments = ((ParameterizedType) type).getActualTypeArguments();
            if (!(actualTypeArguments[index] instanceof Class)) {
                return clazz;
            }
            return (Class<T>) actualTypeArguments[index];
        }
    }


    public static String simpleClassName(Class<?> clazz) {
        if (clazz == null) {
            throw new NullPointerException("clazz");
        }
        String className = clazz.getName();
        final int lastDotIdx = className.lastIndexOf(StringConstants.Symbol.POINT);
        if (lastDotIdx > -1) {
            return className.substring(lastDotIdx + 1);
        }
        return className;
    }



    /**
     * 根据class类名生成key前缀
     * @param clazz Class
     * @return key前缀
     */
    public static String genkeyPrefix(Class<?> clazz) {
        return clazz.getSimpleName().concat(StringConstants.Symbol.COLON);
    }

    private static final Field[] EMPTY_FIELD_ARRAY = new Field[0];

    private static final Map<Class<?>, Field[]> DECLARED_FIELDS_CACHE = new ConcurrentHashMap<>(32);

    private static Field[] getDeclaredFields(Class<?> clazz) {
        AssertUtil.notNull(clazz, "Class must not be null");
        Field[] result = DECLARED_FIELDS_CACHE.get(clazz);
        if (Objects.isNull(result)) {
            try {
                result = clazz.getDeclaredFields();
                DECLARED_FIELDS_CACHE.put(clazz, result.length == 0 ? EMPTY_FIELD_ARRAY : result);
            } catch (Throwable e) {
                throw new IllegalStateException("Failed to introspect Class [" + clazz.getName() + "] from ClassLoader [" + clazz.getClassLoader() + "]", e);
            }
        }
        return result;
    }

    public static void doWithFields(Class<?> clazz, FieldCallback fieldCallback) {
        doWithFields(clazz, fieldCallback, null);
    }

    public static void doWithFields(Class<?> clazz, FieldCallback fieldCallback, FieldFilter fieldFilter) {
        Class<?> targetClass = clazz;
        do {
            Field[] fields = getDeclaredFields(targetClass);
            int len = fields.length;
            for (int index = 0; index < len; ++index) {
                Field field = fields[index];
                if (null == fieldFilter || fieldFilter.matches(field)) {
                    try {
                        fieldCallback.doWith(field);
                    } catch (IllegalAccessException var10) {
                        throw new IllegalStateException("Not allowed to access field '" + field.getName() + "': " + var10);
                    }
                }
            }
            targetClass = targetClass.getSuperclass();
        } while (targetClass != null && targetClass != Object.class);
    }

    @SuppressWarnings("deprecation")
    public static void makeAccessible(Field field) {
        if ((!Modifier.isPublic(field.getModifiers()) ||
                !Modifier.isPublic(field.getDeclaringClass().getModifiers()) ||
                Modifier.isFinal(field.getModifiers())) && !field.isAccessible()) {
            field.setAccessible(true);
        }
    }

    @FunctionalInterface
    public interface FieldCallback {
        void doWith(Field field) throws IllegalArgumentException, IllegalAccessException;
    }

    @FunctionalInterface
    public interface FieldFilter {
        boolean matches(Field field);
    }




}
