package com.github.restup.util;

import com.github.restup.path.ResourcePath;
import com.google.common.collect.Iterables;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import org.apache.commons.lang3.ArrayUtils;

public class UpUtils {

    private UpUtils() {

    }

    /**
     * Null safe unmodifiable list returning empty list for null.
     * 
     * @param <T> type of elements in the list
     * @param list to make unmodifiable
     * @return unmodifiable list, never null
     */
    public static <T> List<T> unmodifiableList(List<T> list) {
        return list == null ? Collections.emptyList() : Collections.unmodifiableList(list);
    }

    public static <K, V> Map<K, V> unmodifiableMap(Map<K, V> map) {
        return map == null ? Collections.emptyMap() : Collections.unmodifiableMap(map);
    }

    public static <T> void removeAll(List<T> target, List<T> source) {
        if (source != null) {
            target.removeAll(source);
        }
    }

    public static void addAll(List<ResourcePath> target, List<ResourcePath> source) {
        if (source != null) {
            target.addAll(source);
        }
    }

    public static <T> T getFirst(Iterable<T> it) {
        return getFirst(it, null);
    }

    public static <T> T getFirst(Iterable<T> it, T defaultValue) {
        return it == null ? defaultValue : Iterables.getFirst(it, defaultValue);
    }

    public static void put(Map<String, String[]> map, String name, String value) {
        String[] arr = map.get(name);
        if (arr == null) {
            map.put(name, new String[]{value});
        } else {
            map.put(name, ArrayUtils.add(arr, value));
        }
    }

    public static <T> T nvl(T a, T b) {
        return a == null ? b : a;
    }

    public static <T> T nvl(T a, Supplier<T> b) {
        return a == null ? b.get() : a;
    }

    public static String[] names(Class<? extends Enum<?>> e) {
        return Arrays.stream(e.getEnumConstants())
            .map(Enum::name)
            .toArray(String[]::new);
    }

    public static String[] names(String pattern, Class<? extends Enum<?>> e) {
        return Arrays.stream(e.getEnumConstants())
            .map((value) -> MessageFormat.format(pattern, value.name()))
            .toArray(String[]::new);
    }

}
