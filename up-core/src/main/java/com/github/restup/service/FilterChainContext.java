package com.github.restup.service;

import com.github.restup.bind.MethodArgumentFactory;
import com.github.restup.bind.param.ParameterProvider;
import com.github.restup.errors.ErrorFactory;
import com.github.restup.errors.Errors;
import com.github.restup.query.PreparedResourceQueryStatement;
import com.github.restup.query.ResourceQueryDefaults;
import com.github.restup.query.ResourceQueryStatement;
import com.github.restup.registry.Resource;
import com.github.restup.service.model.request.AbstractRequest;
import com.github.restup.service.model.request.PersistenceRequest;
import com.github.restup.service.model.request.QueryRequest;
import com.github.restup.service.model.request.ResourceRequest;
import com.github.restup.util.Assert;

import java.util.ArrayList;
import java.util.List;

public class FilterChainContext implements MethodArgumentFactory {

    private final MethodArgumentFactory argumentFactory;
    private final ErrorFactory errorFactory;
    private final Object[] arguments;
    private final Class<?>[] argumentTypes;
    private Errors errors;
    private PersistenceRequest<?> persistenceRequest;
    private ParameterProvider parameterProvider;
    private Object persistedState;
    private Resource<?, ?> resource;
    private ResourceQueryStatement query;
    private ResourceQueryDefaults template;
    private List<Object> added;

    public FilterChainContext(MethodArgumentFactory argumentFactory, ErrorFactory errorFactory, Class<?>[] argumentTypes) {
        super();
        Assert.notNull(argumentFactory, "argumentFactory is required");
        Assert.notNull(errorFactory, "errorFactory is required");
        Assert.notNull(argumentTypes, "argumentTypes is required");
        this.argumentFactory = argumentFactory;
        this.errorFactory = errorFactory;
        this.argumentTypes = argumentTypes;
        this.arguments = new Object[argumentTypes.length];
        added = new ArrayList<>();
    }

    public Object getArgument(int i) {
        Object result = arguments[i];
        if (result == null) {
            Class<?> type = argumentTypes[i];
            result = newInstance(type);
            // add argument so the new instances is both assigned
            // to this index as well as any other for which it may apply
            addArgument(result);
        }
        return result;
    }

    public void addArguments(Object... args) {
        for (Object arg : args) {
            addArgument(arg);
        }
    }

    @SuppressWarnings("rawtypes")
    public void addArgument(Object arg) {
        if (arg != null) {
            if (arg instanceof Object[]) {
                for (Object o : (Object[]) arg) {
                    addArgument(o);
                }
                return;
            }
            if (arg instanceof Iterable) {
                for (Object o : (Iterable) arg) {
                    addArgument(o);
                }
                return;
            }
            Class<?> type = arg.getClass();
            for (int i = 0; i < argumentTypes.length; i++) {
                if (argumentTypes[i].isAssignableFrom(type)) {
                    arguments[i] = arg;
                }
            }
            if (arg instanceof PersistenceRequest) {
                addArgument(((PersistenceRequest) arg).getData());
            }
            if (arg instanceof Resource) {
                this.resource = (Resource) arg;
                addArgument(resource.getMapping());
            }
            if (arg instanceof PersistenceRequest) {
                this.persistenceRequest = (PersistenceRequest) arg;
            }
            if (arg instanceof Errors) {
                this.errors = (Errors) arg;
            }
            if (arg instanceof ParameterProvider) {
                this.parameterProvider = (ParameterProvider) arg;
            }
            if (arg instanceof AbstractRequest) {
                addArguments(((AbstractRequest) arg).getDelegate());
            }
            if (arg instanceof ResourceRequest) {
                ResourceRequest request = (ResourceRequest) arg;
                addArgument(request.getResource());
            }
            if (arg instanceof QueryRequest) {
                QueryRequest request = (QueryRequest) arg;
                addArgument(request.getQuery());
            }
            if (arg instanceof ResourceQueryStatement) {
                this.query = (ResourceQueryStatement) arg;
            }
            if (arg instanceof ResourceQueryDefaults) {
                this.template = (ResourceQueryDefaults) arg;
            }
            added.add(arg);
        }
    }

    private Errors getErrors() {
        if (errors == null) {
            errors = errorFactory.createErrors();
        }
        return errors;
    }

    public ParameterProvider getParameterProvider() {
        return parameterProvider;
    }

    @SuppressWarnings("unchecked")
    public <T> T newInstance(Class<T> clazz) {
        if (Errors.class.isAssignableFrom(clazz)) {
            return (T) getErrors();
        } else if (FilterChainContext.class.isAssignableFrom(clazz)) {
            return (T) this;
        } else if (PreparedResourceQueryStatement.class.isAssignableFrom(clazz)) {
            return (T) new PreparedResourceQueryStatement(resource, query, template);
        } else if (clazz == ResourceQueryDefaults.class) {
            return (T) new ResourceQueryDefaults(resource, query);
        } else {
            for ( Object o : added ) {
                if ( clazz.isAssignableFrom(o.getClass())) {
                    return (T)o;
                }
            }
            return newInstance(clazz, this, getErrors());
        }
    }

    public <T> T newInstance(Class<T> clazz, FilterChainContext ctx, Errors errors) {
        return argumentFactory.newInstance(clazz, ctx, errors);
    }

    public boolean hasErrors() {
        return errors != null && errors.hasErrors();
    }

    public void assertErrors() {
        if (errors != null) {
            errors.assertErrors();
        }
    }

    public PersistenceRequest<?> getPersistenceRequest() {
        return persistenceRequest;
    }

    public Object getPersistedState() {
        return persistedState;
    }

    public void setPersistedState(Object persistedState) {
        this.persistedState = persistedState;
    }

}
