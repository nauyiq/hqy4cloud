package com.hqy.cloud.util;

import java.lang.reflect.Field;

/**
 * The utilities class for Java Reflection {@link Field}
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/6/28 10:47
 */
public interface FieldUtils {

    /**
     * Like the {@link Class#getDeclaredField(String)} method without throwing any {@link Exception}
     *
     * @param declaredClass the declared class
     * @param fieldName     the name of {@link Field}
     * @return if field can't be found, return <code>null</code>
     */
    static Field getDeclaredField(Class<?> declaredClass, String fieldName) {
        try {
            Field[] fields = declaredClass.getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                if (fields[i].getName().equals(fieldName)) {
                    return fields[i];
                }
            }
            return null;
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    /**
     * Find the {@link Field} by the name in the specified class and its inherited types
     *
     * @param declaredClass the declared class
     * @param fieldName     the name of {@link Field}
     * @return if can't be found, return <code>null</code>
     */
    /*static Field findField(Class<?> declaredClass, String fieldName) {
        Field field = getDeclaredField(declaredClass, fieldName);
        if (field != null) {
            return field;
        }
        for (Class superType : getAllInheritedTypes(declaredClass)) {
            field = getDeclaredField(superType, fieldName);
            if (field != null) {
                break;
            }
        }

        if (field == null) {
            throw new IllegalStateException(String.format("cannot find field %s,field is null", fieldName));
        }

        return field;
    }*/

    /**
     * Find the {@link Field} by the name in the specified class and its inherited types
     *
     * @param object    the object whose field should be modified
     * @param fieldName the name of {@link Field}
     * @return if can't be found, return <code>null</code>
     */
    static Field findField(Object object, String fieldName) {
        return findField(object.getClass(), fieldName);
    }

    /**
     * Get the value of the specified {@link Field}
     *
     * @param object    the object whose field should be modified
     * @param fieldName the name of {@link Field}
     * @return the value of  the specified {@link Field}
     */
    static Object getFieldValue(Object object, String fieldName) {
        return getFieldValue(object, findField(object, fieldName));
    }

    /**
     * Get the value of the specified {@link Field}
     *
     * @param object the object whose field should be modified
     * @param field  {@link Field}
     * @return the value of  the specified {@link Field}
     */
    static <T> T getFieldValue(Object object, Field field) {
        boolean accessible = field.isAccessible();
        Object value = null;
        try {
            if (!accessible) {
                field.setAccessible(true);
            }
            value = field.get(object);
        } catch (IllegalAccessException ignored) {
        } finally {
            field.setAccessible(accessible);
        }
        return (T) value;
    }

    /**
     * Set the value for the specified {@link Field}
     *
     * @param object    the object whose field should be modified
     * @param fieldName the name of {@link Field}
     * @param value     the value of field to be set
     * @return the previous value of the specified {@link Field}
     */
    static <T> T setFieldValue(Object object, String fieldName, T value) {
        return setFieldValue(object, findField(object, fieldName), value);
    }

    /**
     * Set the value for the specified {@link Field}
     *
     * @param object the object whose field should be modified
     * @param field  {@link Field}
     * @param value  the value of field to be set
     * @return the previous value of the specified {@link Field}
     */
    static <T> T setFieldValue(Object object, Field field, T value) {
        boolean accessible = field.isAccessible();
        Object previousValue = null;
        try {
            if (!accessible) {
                field.setAccessible(true);
            }
            previousValue = field.get(object);
            field.set(object, value);
        } catch (IllegalAccessException ignored) {
        } finally {
            field.setAccessible(accessible);
        }
        return (T) previousValue;
    }


}
