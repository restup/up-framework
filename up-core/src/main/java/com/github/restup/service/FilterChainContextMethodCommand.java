package com.github.restup.service;

import com.github.restup.util.Assert;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * {@link IndexedVarArgsMethodCommand} which obtains method arguments at runtime from a {@link FilterChainContext} index.
 *
 * The command is expected to be executed with the FilterChainContext as a first argument, all other arguments are ignored.  The arguments for the {@link #method} invoked are obtained by index using {@link FilterChainContext#getArgument(int)}
 *
 * @author andy.buttaro
 */
public class FilterChainContextMethodCommand extends IndexedVarArgsMethodCommand {

    private final String label;

    public FilterChainContextMethodCommand(Object objectInstance, Class<? extends Annotation> methodAnnotation, Method method, Object[] arguments) {
        super(objectInstance, method, arguments);
        Assert.notNull(methodAnnotation, "methodAnnotation may not be null");
        label = methodAnnotation.getSimpleName();
    }

    @Override
    protected Object getArg(Object[] args, Integer idx) {
        FilterChainContext ctx = (FilterChainContext) args[0];
        return ctx.getArgument(idx);
    }

    public String getLabel() {
        return label;
    }

}
