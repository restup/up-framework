package com.github.restup.service;

import com.github.restup.errors.ErrorBuilder;
import com.github.restup.errors.ErrorObjectException;
import com.github.restup.util.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static com.github.restup.util.ReflectionUtils.findAnnotatedMethod;

/**
 * Simple Method Executor which uses reflection to invoke a {@link Method} from a variable
 * list of arguments which are mapped to the {@link Method} arguments by type.
 * <p>
 * For example, given the class Foo
 * <pre class="code">
 * public class Foo {
 * public void bar(String message) {
 * System.out.println(message);
 * }
 * }
 * </pre>
 * <p>
 * The following will print "Hello World"
 * <p>
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
        Assert.notNull(objectInstance, "An object instance is required");
        Assert.notNull(method, "An Method is required");
        this.objectInstance = objectInstance;
        method.setAccessible(true);
        this.method = method;
    }

    public VarArgsMethodCommand(Class<? extends Annotation> methodAnnotation, Object... objects) {
        Assert.notNull(methodAnnotation, "An annotation class is required");
        Method method = null;
        Object objectInstance = null;
        for (Object o : objects) {
            method = findAnnotatedMethod(o.getClass(), methodAnnotation);
            if (method != null) {
                objectInstance = o;
                break;
            }
        }
        Assert.notNull(objectInstance, "An object instance is required");
        Assert.notNull(method, "An Method instance is required");
        this.objectInstance = objectInstance;
        this.method = method;
    }

    /**
     * Finds appropriate method by simply iterating through all args to find
     * and matching values.
     *
     * @param method to be executed
     * @param args   passed as arguments to {@link #execute(Object...)}
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

    public Object execute(Object... args) {
        try {
            Object[] params = mapArgs(method, args);
            if (log.isDebugEnabled()) {
                debug(params);
            }
            return method.invoke(objectInstance, params);
        } catch (IllegalAccessException e) {
            throw handle(e);
        } catch (IllegalArgumentException e) {
            throw handle(e);
        } catch (InvocationTargetException e) {
            if (e.getTargetException() instanceof ErrorObjectException) {
                throw (ErrorObjectException) e.getTargetException();
            }
            throw handle(e);
        }
    }

    protected RuntimeException handle(Throwable t) {
        if (t.getCause() instanceof ErrorObjectException) {
            return (ErrorObjectException) t.getCause();
        }
        return ErrorBuilder.buildException(t);
    }

    public Object getObjectInstance() {
        return objectInstance;
    }

    public Method getMethod() {
        return method;
    }
}
