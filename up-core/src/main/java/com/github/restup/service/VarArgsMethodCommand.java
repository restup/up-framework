package com.github.restup.service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.github.restup.errors.RequestErrorException;
import com.github.restup.util.Assert;

/**
 * Simple Method Executor which uses reflection to invoke a {@link Method} from a variable list of
 * arguments which are mapped to the {@link Method} arguments by type.
 * <p>
 * For example, given the class Foo
 * </p>
 * 
 * <pre class="code">
 * public class Foo {
 *     public void bar(String message) {
 *         System.out.println(message);
 *     }
 * }
 * </pre>
 * <p>
 * The following will print "Hello World"
 * </p>
 * 
 * <pre class="code">
 * VarArgsMethodExecutor executor = new VarArgsMethodExecutor(new Foo(), Foo.class.getMethod("bar", String.class));
 * executor.execute(new Date(), 10, "Hello World");
 * </pre>
 *
 * @author andy.buttaro
 */
public class VarArgsMethodCommand implements MethodCommand<Object> {

    private final static Logger log = LoggerFactory.getLogger(VarArgsMethodCommand.class);

    private final Object objectInstance;
    private final Method method;

    public VarArgsMethodCommand(Object objectInstance, Method method) {
        super();
        assertArguments(objectInstance, method);
        this.objectInstance = objectInstance;
        this.method = method;
    }
    
    static void assertArguments(Object objectInstance, Method method) {
        Assert.notNull(objectInstance, "An object instance is required");
        Assert.notNull(method, "An Method instance is required");
        method.setAccessible(true);
    }

    /**
     * Finds appropriate method by simply iterating through all args to find and matching values.
     *
     * @param method to be executed
     * @param args passed as arguments to {@link #execute(Object...)}
     * @return the correct arguments for method from args.
     */
    protected Object[] mapArgs(Method method, Object[] args) {
        int size = method.getParameterCount();
        Object[] params = new Object[size];

        for (int i = 0; i < params.length; i++) {
            Class<?> clazz = method.getParameterTypes()[i];
            for (Object o : args) {
                if (o != null && clazz.isAssignableFrom(o.getClass())) {
                    if (params[i] == null) {
                        params[i] = o;
                    }
                }
            }
        }
        return params;
    }

    protected void debug(Object[] params) {
        log.debug("executing {}.{}(...)", objectInstance.getClass(), method.getName());
    }

    @Override
    public Object execute(Object... args) {
        try {
            Object[] params = mapArgs(method, args);
            if (log.isDebugEnabled()) {
                debug(params);
            }
            return method.invoke(objectInstance, params);
        } catch (IllegalAccessException | IllegalArgumentException e) {
            throw handle(e);
        } catch (InvocationTargetException e) {
            if (e.getTargetException() instanceof RequestErrorException) {
                throw (RequestErrorException) e.getTargetException();
            }
            throw handle(e);
        }
    }

    protected RuntimeException handle(Throwable t) {
        if (t.getCause() instanceof RequestErrorException) {
            return (RequestErrorException) t.getCause();
        }
        return RequestErrorException.of(t);
    }

    public Object getObjectInstance() {
        return objectInstance;
    }

    public Method getMethod() {
        return method;
    }
}
