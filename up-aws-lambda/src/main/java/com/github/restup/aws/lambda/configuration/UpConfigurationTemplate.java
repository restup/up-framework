package com.github.restup.aws.lambda.configuration;

import com.github.restup.bind.converter.ConverterFactory;
import com.github.restup.errors.ErrorFactory;
import com.github.restup.mapping.MappedClassFactory;
import com.github.restup.query.Pagination;
import com.github.restup.registry.ResourceRegistry;
import com.github.restup.registry.ResourceRegistryBuilderDecorator;
import com.github.restup.registry.settings.ControllerMethodAccess;
import com.github.restup.registry.settings.ServiceMethodAccess;
import com.github.restup.service.model.request.RequestObjectFactory;

abstract public class UpConfigurationTemplate {

    private final UpProperties props;

    public UpConfigurationTemplate(UpProperties props) {
        super();
        this.props = props;
    }

    public ControllerMethodAccess getControllerMethodAccess() {
        return ControllerMethodAccess.allEnabled();
    }

    public ServiceMethodAccess getServiceMethodAccess() {
        return ServiceMethodAccess.allEnabled();
    }

    public ConverterFactory getConverterFactory() {
        return ConverterFactory.getDefaultConverterFactory();
    }

    public ErrorFactory getErrorFactory() {
        return ErrorFactory.getDefaultErrorFactory();
    }

    public Pagination getPagination() {
        if (props.isPaginationDisabled()) {
            return Pagination.disabled();
        }
        return Pagination.of(props.getMaxPageLimit(),
            props.getDefaultPageLimit(), 0,
            props.isPaginationTotalsDisabled());
    }

    public RequestObjectFactory getRequestObjectFactory() {
        return RequestObjectFactory.getDefaultRequestObjectFactory();
    }
    
    public ResourceRegistryBuilderDecorator getResourceRegistryBuilderDecorator() {
        return (b) -> b;
    }

    public ResourceRegistry getRegistry() throws ClassNotFoundException {

        return getResourceRegistryBuilderDecorator().decorate(
            ResourceRegistry.builder()
                .basePath(props.getBasePath())
                .excludeFrameworkFilters(props.isExcludeFrameworkFilters())
                .controllerMethodAccess(getControllerMethodAccess())
                .defaultPagination(getPagination())
                .errorFactory(getErrorFactory())
                .converterFactory(getConverterFactory())
                .mappedClassFactory(getMappedClassFactory())
                .requestObjectFactory(getRequestObjectFactory())
                .serviceMethodAccess(getServiceMethodAccess())
        ).build();
    }

    abstract MappedClassFactory getMappedClassFactory();

}
