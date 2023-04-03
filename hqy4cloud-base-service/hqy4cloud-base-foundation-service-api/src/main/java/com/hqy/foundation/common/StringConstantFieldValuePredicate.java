package com.hqy.foundation.common;

import java.lang.reflect.Field;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.hqy.cloud.util.FieldUtils.getFieldValue;
import static java.lang.reflect.Modifier.*;

/**
 * The constant field value {@link Predicate} for the specified {@link Class}
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/6/28 10:43
 */
public class StringConstantFieldValuePredicate implements Predicate<String> {

    private final Set<String> constantFieldValues;

    public StringConstantFieldValuePredicate(Class<?> targetClass) {
        this.constantFieldValues = getConstantFieldValues(targetClass);
    }

    public static Predicate<String> of(Class<?> targetClass) {
        return new StringConstantFieldValuePredicate(targetClass);
    }

    private Set<String> getConstantFieldValues(Class<?> targetClass) {
        return Stream.of(targetClass.getFields())
                // static
                .filter(f -> isStatic(f.getModifiers()))
                // public
                .filter(f -> isPublic(f.getModifiers()))
                // final
                .filter(f -> isFinal(f.getModifiers()))
                // filters String type
                .map(this::getConstantValue)
                .filter(v -> v instanceof String)
                // Casts String type
                .map(String.class::cast)
                .collect(Collectors.toSet());
    }

    private Object getConstantValue(Field field) {
        return getFieldValue(null, field);
    }

    @Override
    public boolean test(String s) {
        return constantFieldValues.contains(s);
    }
}
