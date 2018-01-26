package com.github.restup.util;

import java.util.Arrays;
import java.util.function.Consumer;

public class Streams {

	public static <T> void forEach(T[] array, Consumer<? super T> action) {
		Arrays.stream(array).forEach(action);
	}
	
	private Streams() {
	  
	}
	
}
