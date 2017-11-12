package com.github.restup.service;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Builds a unique set of arguments for a set of {@link Method}s
 *
 * @author abuttaro
 */
public class MethodArgumentsBuilder {

    private final List<Class<?>> types;

    public MethodArgumentsBuilder() {
        types = new ArrayList<Class<?>>();
    }

    public void addArgument(Method method) {
        if (method != null) {
            addArguments(method.getParameterTypes());
            addArgument(method.getReturnType());
        }
    }

    public void addArguments(List<Method> methods) {
        if (methods != null) {
            for (Method m : methods) {
                addArgument(m);
            }
        }
    }

    public void addArguments(Method... methods) {
        if (methods != null) {
            for (Method m : methods) {
                addArgument(m);
            }
        }
    }

    public void addArguments(Class<?>... classes) {
        for (Class<?> c : classes) {
            addArgument(c);
        }
    }

    public void addArgument(Class<?> type) {
        int i = 0;
        if (type == Void.TYPE || type == Object.class) {
            return;
        }
        for (Class<?> c : types) {
            if (c == type) {
                return;
            }
            if (!c.isInterface()) {
                if (c.isAssignableFrom(type)) {
                    return;
                }
            }
            if (type.isAssignableFrom(c)) {
                types.set(i, type);
                return;
            }
            i++;
        }
        types.add(type);
    }

    public Class<?>[] build() {
        Class<?>[] result = types.toArray(new Class<?>[types.size()]);
        types.clear();
        return result;
    }
}
