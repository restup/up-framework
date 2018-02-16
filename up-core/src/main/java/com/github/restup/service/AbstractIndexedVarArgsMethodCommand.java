package com.github.restup.service;

import java.lang.reflect.Method;

/**
 * A method executor that assumes that args will be of consistent type and order as is frequently the case. Rather than iterate through methods, The arguments will be assigned by index
 *
 * @author andy.buttaro
 */
public abstract class AbstractIndexedVarArgsMethodCommand extends VarArgsMethodCommand {

    protected AbstractIndexedVarArgsMethodCommand(Object objectInstance, Method method) {
        super(objectInstance, method);
    }

    /**
     * @param method to map args to
     * @param varArgs passed as arguments to {@link #execute(Object...)}
     * @return the mapped indexes of passed args to executed args
     */
    abstract protected Integer[] getIndexes(Method method, Object[] varArgs);

    /**
     * Maps values from args based upon the indexes retruned by {@link #getIndexes(Method, Object[])}. So, arguments may be [ String.class, Long.class ] and the method may accept Long as an argument, thus index[0] is expected to be 1 and [Long] will be returned.
     */
    @Override
    protected Object[] mapArgs(Method method, Object[] args) {

        // get the mapped indexes
        Integer[] indexes = getIndexes(method, args);

        // create a result array
        int size = method.getParameterTypes().length;
        Object[] params = new Object[size];

        for (int i = 0; i < indexes.length; i++) {
            Integer idx = indexes[i];
            if (idx != null) {
                // set the param value to the value for the index
                params[i] = getArg(args, idx);
            }
        }
        return params;
    }

    /**
     * @param args passed as arguments to {@link #execute(Object...)}
     * @param idx obtained by method argument index for the args index
     * @return args[idx]
     */
    protected Object getArg(Object[] args, Integer idx) {
        return args[idx];
    }

    protected synchronized Integer[] bind(Method method, Object[] args) {
        Integer[] indexes = new Integer[method.getParameterTypes().length];
        Class<?>[] types = getMethod().getParameterTypes();
        for (int i = 0; i < indexes.length; i++) {
            Class<?> clazz = types[i];
            for (int n = 0; n < args.length; n++) {
                Object o = args[n];
                if (o != null) {
                    if (clazz.isAssignableFrom(o.getClass()) || clazz == o) {
                        indexes[i] = n;
                        break;
                    }
                }
            }
        }
        return indexes;
    }

}
