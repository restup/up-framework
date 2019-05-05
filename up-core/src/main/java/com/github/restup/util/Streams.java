package com.github.restup.util;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

public class Streams {

    private Streams() {

    }

    public static <T> void forEach(T[] array, Consumer<? super T> action) {
        Arrays.stream(array).forEach(action);
    }

    public static <T> void forEachNonNull(T[] array, Consumer<? super T> action) {
        Arrays.stream(array).filter(Objects::nonNull).forEach(action);
    }

    public static <T> void forEachNonNull(Set<T> c, Consumer<? super T> action) {
        c.stream().filter(Objects::nonNull).forEach(action);
    }
	
}
