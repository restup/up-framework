package com.github.restup.util;

import com.github.restup.path.ResourcePath;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.ArrayUtils;

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

    @SuppressWarnings("unchecked")
    public static <T> Set<T> unmodifiableSet(Set<T> map) {
        return map == null ? Collections.EMPTY_SET : Collections.unmodifiableSet(map);
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
        return it == null ? defaultValue : getFirst(it.iterator(), defaultValue);
    }

    public static <T> T getFirst(Iterator<T> it, T defaultValue) {
        return it == null || !it.hasNext() ? defaultValue : it.next();
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
