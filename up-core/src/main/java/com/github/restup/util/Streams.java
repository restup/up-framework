package com.github.restup.util;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Consumer;

public class Streams {

    public static <T> void forEach(T[] array, Consumer<? super T> action) {
        Arrays.stream(array).forEach(action);
    }

    public static <T> void forEachNonNull(T[] array, Consumer<? super T> action) {
        Arrays.stream(array).filter(Objects::nonNull).forEach(action);
    }
	
	private Streams() {
	  
	}
	
}
