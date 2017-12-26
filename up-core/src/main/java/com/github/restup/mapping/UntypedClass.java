package com.github.restup.mapping;

import java.lang.reflect.Type;

import com.github.restup.util.ReflectionUtils;

/**
 * Defines a unique type for typeless resource objects
 * 
 * @author abuttaro
 *
 */
public class UntypedClass<T> implements Type {
	
	private final Class<T> container;
	
	public UntypedClass(Class<T> container) {
		this.container = container;
	}
	
	public T newInstance() {
		return ReflectionUtils.newInstance(container);
	}
	
	public Class<T> getContainer() {
		return container;
	}

}
