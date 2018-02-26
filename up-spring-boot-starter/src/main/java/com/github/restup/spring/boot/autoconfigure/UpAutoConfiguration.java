package com.github.restup.spring.boot.autoconfigure;

import java.util.List;
import java.util.Set;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.domain.EntityScanPackages;
import org.springframework.boot.autoconfigure.domain.EntityScanner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.github.restup.annotations.ApiName;
import com.github.restup.annotations.Plural;
import com.github.restup.annotations.Resource;
import com.github.restup.bind.converter.ConverterFactory;
import com.github.restup.errors.ErrorFactory;
import com.github.restup.mapping.DefaultMappedClassFactory;
import com.github.restup.mapping.MappedClass;
import com.github.restup.mapping.MappedClassFactory;
import com.github.restup.mapping.fields.DefaultMappedFieldFactory;
import com.github.restup.mapping.fields.MappedFieldBuilderVisitor;
import com.github.restup.query.Pagination;
import com.github.restup.registry.ResourceRegistry;
import com.github.restup.registry.settings.ControllerMethodAccess;
import com.github.restup.registry.settings.ServiceMethodAccess;
import com.github.restup.repository.RepositoryFactory;
import com.github.restup.service.model.request.RequestObjectFactory;
import com.github.restup.spring.boot.autoconfigure.factory.RestrictedFieldsProviderFactory;
import com.github.restup.spring.boot.autoconfigure.factory.ServiceFilterFactory;
import com.github.restup.spring.boot.autoconfigure.factory.SparseFieldsProviderFactory;

@Configuration
@ConditionalOnClass(ResourceRegistry.class)
@EnableConfigurationProperties(UpProperties.class)
public class UpAutoConfiguration {

    private final UpProperties props;
    private final ApplicationContext applicationContext;

    public UpAutoConfiguration(UpProperties props, ApplicationContext applicationContext) {
        super();
        this.props = props;
        this.applicationContext = applicationContext;
    }

    @Bean
    @ConditionalOnMissingBean
    public ControllerMethodAccess defaultUpControllerMethodAccess() {
        return ControllerMethodAccess.allEnabled();
    }

    @Bean
    @ConditionalOnMissingBean(ignored = ControllerMethodAccess.class)
    public ServiceMethodAccess defaultUpServiceMethodAccess() {
        return ServiceMethodAccess.allEnabled();
    }

    @Bean
    @ConditionalOnMissingBean
    public ConverterFactory defaultUpConverterFactory() {
        return ConverterFactory.getDefaultConverterFactory();
    }

    @Bean
    @ConditionalOnMissingBean
    public ErrorFactory defaultUpErrorFactory() {
        return ErrorFactory.getDefaultErrorFactory();
    }

    @Bean
    @ConditionalOnMissingBean
    public Pagination defaultUpPagination() {
        if (props.isPaginationDisabled()) {
            return Pagination.disabled();
        }
        return Pagination.of(props.getMaxPageLimit(),
                props.getDefaultPageLimit(), 0,
                props.isPaginationTotalsDisabled());
    }

    @Bean
    @ConditionalOnMissingBean
    public MappedClassFactory defaultUpMappedClassFactory() {
        MappedFieldBuilderVisitor[] visitors = MappedFieldBuilderVisitor.getDefaultVisitors();
        DefaultMappedFieldFactory mappedFieldFactory = new DefaultMappedFieldFactory(visitors);
        return new DefaultMappedClassFactory(mappedFieldFactory, getPackages(), MappedClass.getDefaultFieldComparator());
    }

    @Bean
    @ConditionalOnMissingBean
    public RequestObjectFactory defaultUpRequestObjectFactory() {
        return RequestObjectFactory.getDefaultRequestObjectFactory();
    }

    @Bean
    @ConditionalOnMissingBean
    public ServiceFilterFactory defaultUpServiceFilterFactory() {
        return new ServiceFilterFactory() {};
    }

    @Bean
    @ConditionalOnMissingBean
    public RestrictedFieldsProviderFactory defaultUpRestrictedFieldsProviderFactory() {
        return new RestrictedFieldsProviderFactory() {};
    }

    @Bean
    @ConditionalOnMissingBean
    public SparseFieldsProviderFactory defaultUpSparseFieldsProviderFactory() {
        return new SparseFieldsProviderFactory() {};
    }

    @Bean
    @ConditionalOnMissingBean
    public ResourceRegistry defaultUpRegistry(
            ControllerMethodAccess controllerMethodAccess,
            ConverterFactory converterFactory,
            ErrorFactory errorFactory,
            MappedClassFactory mappedClassFactory,
            Pagination pagination,
            RepositoryFactory repositoryFactory,
            RequestObjectFactory requestObjectFactory,
            ServiceMethodAccess serviceMethodAccess,
            ServiceFilterFactory serviceFilterFactory,
            RestrictedFieldsProviderFactory restrictedFieldsProviderFactory,
            SparseFieldsProviderFactory sparseFieldsProviderFactory) throws ClassNotFoundException {

        ResourceRegistry registry = ResourceRegistry.builder()
                .basePath(props.getBasePath())
                .excludeFrameworkFilters(props.isExcludeFrameworkFilters())
                .packagesToScan("com.foo", "com.bar")
                .controllerMethodAccess(controllerMethodAccess)
                .defaultPagination(pagination)
                .errorFactory(errorFactory)
                .mappedClassFactory(mappedClassFactory)
                .converterFactory(converterFactory)
                .repositoryFactory(repositoryFactory)
                .requestObjectFactory(requestObjectFactory)
                .serviceMethodAccess(serviceMethodAccess)
                .defaultServiceFilters(serviceFilterFactory.getServiceFilters())
                .defaultRestrictedFieldsProvider(restrictedFieldsProviderFactory.getRestrictedFieldsProvider())
                .defaultSparseFieldsProvider(sparseFieldsProviderFactory.getSparseFieldsProvider())
                .build();

        registry.registerResources(getResources());

        return registry;
    }

    private Set<Class<?>> getResources() throws ClassNotFoundException {
        return new EntityScanner(this.applicationContext)
                .scan(Resource.class, ApiName.class, Plural.class);
    }

    private List<String> getPackages() {
        List<String> packages = EntityScanPackages.get(this.applicationContext).getPackageNames();
        if (packages.isEmpty() && AutoConfigurationPackages.has(this.applicationContext)) {
            packages = AutoConfigurationPackages.get(this.applicationContext);
        }
        return packages;
    }

}
