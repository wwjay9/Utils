package wwjay.demo.utils;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.beans.FeatureDescriptor;
import java.util.List;
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
    public static <T> List<T> copyList(List<T> source, Supplier<T> supplier) {
        if (source == null) {
            return null;
        }
        return source.stream().map(copyFunction(supplier)).collect(Collectors.toList());
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
     * 将一个对象拷贝为另一个对象
     *
     * @param supplier 新类型生成器
     * @param addition 拷贝对象后的额外操作
     * @return 新类型List
     */
    public static <T, R> Function<T, R> copyFunction(Supplier<R> supplier, BiConsumer<T, R> addition) {
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
     * 将source对象中不为空的属性值复制到target对象中
     *
     * @param source 源对象
     * @param target 目标对象
     */
    public static void copyNotNullProperties(Object source, Object target) {
        BeanUtils.copyProperties(source, target, getNullPropertyNames(source));
    }

    /**
     * 获取对象中属性值为null的字段名
     *
     * @param source 源对象
     * @return 字段名数组
     */
    private static String[] getNullPropertyNames(Object source) {
        final BeanWrapper wrappedSource = new BeanWrapperImpl(source);
        return Stream.of(wrappedSource.getPropertyDescriptors())
                .map(FeatureDescriptor::getName)
                .filter(propertyName -> wrappedSource.getPropertyValue(propertyName) == null)
                .toArray(String[]::new);
    }
}
