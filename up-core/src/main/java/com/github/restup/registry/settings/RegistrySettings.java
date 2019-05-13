package com.github.restup.registry.settings;

import com.github.restup.bind.MethodArgumentFactory;
import com.github.restup.bind.converter.ConverterFactory;
import com.github.restup.bind.converter.ParameterConverterFactory;
import com.github.restup.errors.ErrorFactory;
import com.github.restup.mapping.MappedClass;
import com.github.restup.mapping.MappedClassFactory;
import com.github.restup.mapping.MappedClassRegistry;
import com.github.restup.mapping.fields.DefaultMappedFieldFactory;
import com.github.restup.mapping.fields.MappedField;
import com.github.restup.mapping.fields.MappedFieldBuilderVisitor;
import com.github.restup.mapping.fields.MappedFieldFactory;
import com.github.restup.path.ResourcePathsProvider;
import com.github.restup.query.Pagination;
import com.github.restup.registry.Resource;
import com.github.restup.registry.ResourceRegistryRepository;
import com.github.restup.repository.RepositoryFactory;
import com.github.restup.service.filters.BulkOperationByQueryFilter;
import com.github.restup.service.filters.CaseInsensitiveSearchFieldFilter;
import com.github.restup.service.filters.ImmutableFieldValidationFilter;
import com.github.restup.service.filters.IncludeFilter;
import com.github.restup.service.filters.JavaxValidationFilter;
import com.github.restup.service.filters.NotFoundFilter;
import com.github.restup.service.filters.RelationshipValidationFilter;
import com.github.restup.service.filters.SequencedIdValidationFilter;
import com.github.restup.service.model.request.DefaultRequestObjectFactory;
import com.github.restup.service.model.request.RequestObjectFactory;
import java.util.Comparator;
import java.util.List;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Configures settings and implementations to be used by registry.
 */
public interface RegistrySettings {


    static Builder builder() {
		return new Builder();
	}

    ResourceRegistryRepository getResourceRegistryRepository();

    MappedClassRegistry getMappedClassRegistry();

    MappedClassFactory getMappedClassFactory();

    List<String> getPackagesToScan();

    MappedFieldFactory getMappedFieldFactory();

    Comparator<MappedField<?>> getMappedFieldOrderComparator();

    RepositoryFactory getRepositoryFactory();

    MappedFieldBuilderVisitor[] getMappedFieldVisitors();

    ErrorFactory getErrorFactory();

    MethodArgumentFactory getMethodArgumentFactory();

    ConverterFactory getConverterFactory();

    ParameterConverterFactory getParameterConverterFactory();

    ControllerMethodAccess getDefaultControllerAccess();

    ServiceMethodAccess getDefaultServiceAccess();

    List<Object> getDefaultServiceFilters();

    RequestObjectFactory getRequestObjectFactory();

    Pagination getDefaultPagination();

    ResourcePathsProvider getDefaultRestrictedFieldsProvider();

    ResourcePathsProvider getDefaultSparseFieldsProvider();

    String getBasePath();

    static class Builder {
        private final static Logger log = LoggerFactory.getLogger(RegistrySettings.class);

		private boolean excludeFrameworkFilters;

		private ResourceRegistryRepository resourceRegistryMap;
		private MappedClassFactory mappedClassFactory;
		private MappedClassRegistry mappedClassRegistry;
		private String[] packagesToScan;
		private MappedFieldFactory mappedFieldFactory;
			private MappedFieldBuilderVisitor.Builder mappedFieldVisitorBuilder;
		private Comparator<MappedField<?>> mappedFieldOrderComparator;

		private ControllerMethodAccess defaultControllerMethodAccess;
		private ServiceMethodAccess defaultServiceMethodAccess;

		private RepositoryFactory repositoryFactory;
		private ErrorFactory errorFactory;
		private RequestObjectFactory requestObjectFactory;
		private MethodArgumentFactory methodArgumentFactory;
		private Object[] defaultServiceFilters;
		private Pagination defaultPagination;
		private Validator validator;
		private ResourcePathsProvider defaultSparseFieldsProvider;
		private ResourcePathsProvider defaultRestrictedFieldsProvider;
		private String basePath;
        private ConverterFactory converterFactory;

		private Builder me() {
			return this;
		}

		/**
         * Provided alternate storage for registry meta data
         * 
         * @param resourceRegistryMap implementation
         * @return this builder
         */
		public Builder resourceRegistryRepository(ResourceRegistryRepository resourceRegistryMap) {
			this.resourceRegistryMap = resourceRegistryMap;
			return me();
		}

		/**
		 * @param packagesToScan used by {@link com.github.restup.registry.ResourceRegistry} to filter acceptable {@link Resource}s
         * @return this builder
         */
		public Builder packagesToScan(String... packagesToScan) {
			this.packagesToScan = packagesToScan;
			return me();
		}

		/**
         * Comparator for defining sort order of {@link MappedClass#getAttributes()}
         * 
         * @param mappedFieldOrderComparator implementation
         * @return this builder
         */
		public Builder mappedFieldOrderComparator(Comparator<MappedField<?>> mappedFieldOrderComparator) {
			this.mappedFieldOrderComparator = mappedFieldOrderComparator;
			return me();
		}

		/**
         * Provide an alternate implementation for creating {@link MappedField}
         * 
         * @param mappedFieldFactory implementation
         * @return this builder
         */
		public Builder mappedFieldFactory(MappedFieldFactory mappedFieldFactory) {
			this.mappedFieldFactory = mappedFieldFactory;
			return me();
		}

		/**
         * If {@link #mappedFieldFactory(MappedFieldFactory)} is not overridden,
         * {@link MappedFieldBuilderVisitor} implementations may be specified to customize behavior of
         * {@link DefaultMappedFieldFactory}
		 *
		 * @param builder implementations
         * @return this builder
		 */
		public Builder mappedFieldVisitorBuilder(MappedFieldBuilderVisitor.Builder builder) {
			mappedFieldVisitorBuilder = builder;
			return me();
		}

		/**
         * Overrides factory for providing default {@link com.github.restup.repository.Repository}
         * implementations.
         * 
         * @param repositoryFactory implementation
         * @return this builder
         */
		public Builder repositoryFactory(RepositoryFactory repositoryFactory) {
			this.repositoryFactory = repositoryFactory;
			return me();
		}

		/**
         * Overrides factory for providing error objects
         * 
         * @param errorFactory implementation
         * @return this builder
         */
		public Builder errorFactory(ErrorFactory errorFactory) {
			this.errorFactory = errorFactory;
			return me();
		}

		/**
         * Provides argument instances for services filters
         * 
         * @param methodArgumentFactory implementation
         * @return this builder
         */
		public Builder methodArgumentFactory(MethodArgumentFactory methodArgumentFactory) {
			this.methodArgumentFactory = methodArgumentFactory;
			return me();
		}

		/**
         * Defines default service method access for resources. Resources may define their own.
         * 
         * @param defaultServiceMethodAccess implementation
         * @return this builder
         */
		public Builder serviceMethodAccess(ServiceMethodAccess defaultServiceMethodAccess) {
			this.defaultServiceMethodAccess = defaultServiceMethodAccess;
			return me();
		}

		/**
         * Defines default service controller access for resources. Resources may define their own.
         * 
         * @param defaultControllerMethodAccess implementation
         * @return this builder
         */
		public Builder controllerMethodAccess(ControllerMethodAccess defaultControllerMethodAccess) {
			this.defaultControllerMethodAccess = defaultControllerMethodAccess;
			return me();
		}

		/**
         * Overrides default {@link RequestObjectFactory}
         * 
         * @param requestObjectFactory implementation
         * @return this builder
         */
		public Builder requestObjectFactory(RequestObjectFactory requestObjectFactory) {
			this.requestObjectFactory = requestObjectFactory;
			return me();
		}

		/**
         * Overrides default {@link MappedClassFactory}
         * 
         * @param mappedClassFactory implementation
         * @return this builder
         */
		public Builder mappedClassFactory(MappedClassFactory mappedClassFactory) {
			this.mappedClassFactory = mappedClassFactory;
			return me();
		}

		/**
         * If true, default filters ({@link NotFoundFilter}, etc) will be excluded from default filters
         * 
         * @param excludeFrameworkFilters if true service filters are excluded. if false, Up! filters are
         *        added.
         * @return this builder
         */
		public Builder excludeFrameworkFilters(boolean excludeFrameworkFilters) {
			this.excludeFrameworkFilters = excludeFrameworkFilters;
			return me();
		}

		/**
         * Define default service filters to be used for resources relying on filter based services. This
         * will add to default Up! filters unless, {@link #excludeFrameworkFilters(boolean)} is set to true
         * 
         * @param filters to add as default service filters.
         * @return this builder
         */
		public Builder defaultServiceFilters(Object... filters) {
			defaultServiceFilters = filters;
			return me();
		}

        public Builder defaultPagination(Pagination defaultPagination) {
            this.defaultPagination = defaultPagination;
					return me();
        }

        public Builder defaultPaginationDisabled() {
					return defaultPagination(Pagination.disabled());
        }

        public Builder defaultPagination(Integer pageLimit, Integer pageOffset, boolean withTotalsDisabled) {
					return defaultPagination(Pagination.of(pageLimit, pageOffset, withTotalsDisabled));
        }

		public Builder defaultPagination(Integer pageLimit) {
			return defaultPagination(pageLimit, 0, false);
		}

		public Builder validator(Validator validator) {
			this.validator = validator;
			return me();
		}

		/**
         * Default implementation to be used when resource does not specify it's own implementation
         * 
         * @param restrictedFieldsProvider implementation
         * @return this builder
         */
		public Builder defaultRestrictedFieldsProvider(ResourcePathsProvider restrictedFieldsProvider) {
			defaultRestrictedFieldsProvider = restrictedFieldsProvider;
			return me();
		}

		/**
         * Default implementation to be used when resource does not specify it's own implementation
         * 
         * @param defaultSparseFieldsProvider implementation
         * @return this builder
         */
		public Builder defaultSparseFieldsProvider(ResourcePathsProvider defaultSparseFieldsProvider) {
			this.defaultSparseFieldsProvider = defaultSparseFieldsProvider;
			return me();
		}

		/**
         * The default base path for all resources
         * 
         * @param basePath used for exposed endpoints
         * @return this builder
         */
        public Builder basePath(String basePath) {
            this.basePath = basePath;
					return me();
        }

        /**
         * @param converterFactory used for parameter conversion
         * @return this builder
         */
        public Builder converterFactory(ConverterFactory converterFactory) {
            this.converterFactory = converterFactory;
					return me();
        }

		public RegistrySettings build() {
			String[] packagesToScan = this.packagesToScan;
			if (ArrayUtils.isEmpty(packagesToScan)) {
				packagesToScan = new String[] { "com" };
			}
			Comparator<MappedField<?>> mappedFieldOrderComparator = this.mappedFieldOrderComparator;
			if (mappedFieldOrderComparator == null) {
                mappedFieldOrderComparator = MappedClass.getDefaultFieldComparator();
			}
			MappedFieldBuilderVisitor.Builder mappedFieldVisitorBuilder = this.mappedFieldVisitorBuilder;
			if (mappedFieldVisitorBuilder == null) {
				mappedFieldVisitorBuilder = MappedFieldBuilderVisitor.builder().withDefaults();
			}
			MappedFieldBuilderVisitor[] mappedFieldVisitors = mappedFieldVisitorBuilder.build();
			MappedFieldFactory mappedFieldFactory = this.mappedFieldFactory;
			if (mappedFieldFactory == null) {
				mappedFieldFactory = new DefaultMappedFieldFactory(mappedFieldVisitors);
			}

			ErrorFactory errorFactory = this.errorFactory;
			if (errorFactory == null) {
                errorFactory = ErrorFactory.getDefaultErrorFactory();
			}
			RequestObjectFactory requestObjectFactory = this.requestObjectFactory;
			if (requestObjectFactory == null) {
				requestObjectFactory = new DefaultRequestObjectFactory();
			}

			Object[] defaultServiceFilters = this.defaultServiceFilters;

			if (!excludeFrameworkFilters) {
				defaultServiceFilters = ArrayUtils.addAll(defaultServiceFilters, new BulkOperationByQueryFilter(),
						new ImmutableFieldValidationFilter(), new IncludeFilter(), new NotFoundFilter(),
						new RelationshipValidationFilter(), new SequencedIdValidationFilter(),
						new CaseInsensitiveSearchFieldFilter());

				Validator javaxValidations = validator;
				if (javaxValidations == null) {
					try {
						ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
						javaxValidations = factory.getValidator();
					} catch (Exception p) {
						log.warn(
								"Unable to add JavaxValidationFilter, because no Bean Validation provider could be found. Add a provider like Hibernate Validator (RI) to your classpath.");
					}
				}
				if (javaxValidations != null) {
					defaultServiceFilters = ArrayUtils.add(defaultServiceFilters,
							new JavaxValidationFilter(javaxValidations));
				}
			}

			ResourceRegistryRepository resourceRegistryMap = this.resourceRegistryMap;
			if (resourceRegistryMap == null) {
				resourceRegistryMap = new DefaultResourceRegistryRepository();
			}

            // builder.add(parameterConverters);
            ConverterFactory converterFactory = this.converterFactory;

            if (converterFactory == null) {
                converterFactory = ConverterFactory.getDefaultConverterFactory();
			}

			ParameterConverterFactory parameterConverterFactory = ParameterConverterFactory
					.builder(errorFactory)
					.addAll(converterFactory.getConverters(String.class))
					.build();

			ControllerMethodAccess defaultControllerMethodAccess = this.defaultControllerMethodAccess;
			if (defaultControllerMethodAccess == null) {
                defaultControllerMethodAccess = ControllerMethodAccess.allEnabled();
			}

			ServiceMethodAccess defaultServiceMethodAccess = this.defaultServiceMethodAccess;
			if (defaultServiceMethodAccess == null) {
                defaultServiceMethodAccess = ServiceMethodAccess.allEnabled();
			}

			Pagination pagination = defaultPagination;
			if (pagination == null) {
				pagination = Pagination.of(10, 0);
			}

			ResourcePathsProvider defaultSparseFields = defaultSparseFieldsProvider;
			if (defaultSparseFields == null) {
                defaultSparseFields = ResourcePathsProvider.allApiFields();
			}

			ResourcePathsProvider restrictedFields = defaultRestrictedFieldsProvider;
			if (restrictedFields == null) {
                restrictedFields = ResourcePathsProvider.empty();
			}

			String basePath = Resource.cleanBasePath(this.basePath);
			if (basePath == null) {
				basePath = "/";
			}

			return new BasicRegistrySettings(resourceRegistryMap, mappedClassFactory,
				mappedClassRegistry, packagesToScan,
					mappedFieldFactory, mappedFieldVisitors, mappedFieldOrderComparator, defaultControllerMethodAccess,
				defaultServiceMethodAccess, repositoryFactory, errorFactory, requestObjectFactory,
				methodArgumentFactory, converterFactory, parameterConverterFactory,
				defaultServiceFilters,
					pagination, defaultSparseFields, restrictedFields, basePath);
		}
	}

}
