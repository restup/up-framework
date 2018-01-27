package com.github.restup.util;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.ArrayUtils;
import com.github.restup.path.ResourcePath;
import com.google.common.collect.Iterables;

public class UpUtils {

    private UpUtils() {

    }

    /**
     * @return unmodifiable list, never null
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> unmodifiableList(List<T> list) {
        return list == null ? Collections.EMPTY_LIST : Collections.unmodifiableList(list);
    }

    @SuppressWarnings("unchecked")
    public static <K, V> Map<K, V> unmodifiableMap(Map<K, V> map) {
        return map == null ? Collections.EMPTY_MAP : Collections.unmodifiableMap(map);
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

}
