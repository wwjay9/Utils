package com.wwj.util.java.bean;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.util.StringUtils;

import java.beans.FeatureDescriptor;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Bean工具
 *
 * @author wwj
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class BeanUtil {

    private BeanUtil() {
    }

    /**
     * 将一个List拷贝为另一个List
     *
     * @param source   源List
     * @param supplier 新类型生成器
     * @return 新类型List
     */
    public static <T, R> List<R> copyList(List<T> source, Supplier<R> supplier) {
        if (source == null) {
            return new ArrayList<>();
        }
        return source.stream().map(copyFunction(supplier)).collect(Collectors.toList());
    }

    /**
     * 将source对象中不为空的属性值复制到target对象中
     *
     * @param source 源对象
     * @param target 目标对象
     */
    public static void copyNotNullProperties(Object source, Object target) {
        BeanUtils.copyProperties(source, target, getNullPropertyNames(source));
    }

    /**
     * 将source对象中不为null或toString不为空字符串的属性值复制到target对象中
     *
     * @param source 源对象
     * @param target 目标对象
     */
    public static void copyNotEmptyProperties(Object source, Object target) {
        BeanWrapper beanWrapper = PropertyAccessorFactory.forBeanPropertyAccess(source);
        String[] emptyFields = Stream.of(beanWrapper.getPropertyDescriptors())
                .filter(property -> {
                    Object value = beanWrapper.getPropertyValue(property.getName());
                    return value == null || !StringUtils.hasText(value.toString());
                })
                .map(FeatureDescriptor::getName)
                .toArray(String[]::new);
        BeanUtils.copyProperties(source, target, emptyFields);
    }

    /**
     * 判断Bean对象中的所有字段是否为null
     *
     * @param object Bean对象
     * @return 所有字段都为null时返回true，否则返回false
     */
    public static boolean fieldsIsNull(Object object) {
        BeanWrapper beanWrapper = PropertyAccessorFactory.forBeanPropertyAccess(object);
        return Stream.of(beanWrapper.getPropertyDescriptors())
                .map(FeatureDescriptor::getName)
                .filter(propertyName -> !Objects.equals(propertyName, "class"))
                .allMatch(propertyName -> beanWrapper.getPropertyValue(propertyName) == null);
    }

    /**
     * 将一个对象拷贝为另一个对象
     *
     * @param supplier 新类型生成器
     * @return 新类型List
     */
    public static <T, R> Function<T, R> copyFunction(Supplier<R> supplier) {
        return copyFunction(supplier, (t, r) -> {
        });
    }

    /**
     * 将一个对象拷贝为一个新对象
     *
     * @param source    源对象
     * @param newObject 新对象生成器
     * @return 拷贝后的对象
     */
    public static <T> T copyProperties(Object source, Supplier<T> newObject) {
        if (source == null) {
            return null;
        }
        T t = newObject.get();
        BeanUtils.copyProperties(source, t);
        return t;
    }

    /**
     * 将集合转成MAP
     *
     * @param collection 集合
     * @param getKey     map的key
     * @return LinkedHashMap
     */
    public static <K, V> Map<K, V> toMap(Collection<V> collection, Function<V, K> getKey) {
        return collection.stream()
                .collect(Collectors.toMap(getKey, Function.identity(), (o1, o2) -> o2, LinkedHashMap::new));
    }

    /**
     * 将一个对象拷贝为另一个对象
     *
     * @param supplier 新类型生成器
     * @param addition 拷贝对象后的额外操作
     * @return 新类型List
     */
    private static <T, R> Function<T, R> copyFunction(Supplier<R> supplier, BiConsumer<T, R> addition) {
        return t -> {
            if (t == null) {
                return null;
            }
            R r = supplier.get();
            BeanUtils.copyProperties(t, r);
            addition.accept(t, r);
            return r;
        };
    }

    /**
     * 获取对象中属性值为null的字段名
     *
     * @param source 源对象
     * @return 字段名数组
     */
    private static String[] getNullPropertyNames(Object source) {
        BeanWrapper beanWrapper = PropertyAccessorFactory.forBeanPropertyAccess(source);
        return Stream.of(beanWrapper.getPropertyDescriptors())
                .map(FeatureDescriptor::getName)
                .filter(propertyName -> beanWrapper.getPropertyValue(propertyName) == null)
                .toArray(String[]::new);
    }

    /**
     * 判断对象的所有字段是否为null
     *
     * @param obj 对象
     * @return 所有字段都为null时返回true
     */
    public static boolean allFieldIsNull(Object obj) {
        if (obj == null) {
            return true;
        }
        BeanWrapper beanWrapper = PropertyAccessorFactory.forBeanPropertyAccess(obj);
        return Stream.of(beanWrapper.getPropertyDescriptors())
                .map(FeatureDescriptor::getName)
                .filter(name -> !Objects.equals(name, "class"))
                .map(beanWrapper::getPropertyValue)
                .allMatch(Objects::isNull);
    }
}