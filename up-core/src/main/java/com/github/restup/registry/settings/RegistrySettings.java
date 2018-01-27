package com.github.restup.registry.settings;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.github.restup.bind.MethodArgumentFactory;
import com.github.restup.bind.converter.ConverterFactory;
import com.github.restup.bind.converter.ParameterConverter;
import com.github.restup.bind.converter.ParameterConverterFactory;
import com.github.restup.errors.ErrorFactory;
import com.github.restup.mapping.MappedClass;
import com.github.restup.mapping.MappedClassFactory;
import com.github.restup.mapping.MappedClassRegistry;
import com.github.restup.mapping.fields.DefaultMappedFieldFactory;
import com.github.restup.mapping.fields.MappedField;
import com.github.restup.mapping.fields.MappedFieldBuilderVisitor;
import com.github.restup.mapping.fields.MappedFieldFactory;
import com.github.restup.mapping.fields.visitors.IdentityByConventionMappedFieldBuilderVisitor;
import com.github.restup.mapping.fields.visitors.JacksonMappedFieldBuilderVisitor;
import com.github.restup.path.AllResourcePathsProvider;
import com.github.restup.path.EmptyResourcePathsProvider;
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
		private boolean excludeDefaultConverters;

		private ResourceRegistryRepository resourceRegistryMap;
		private MappedClassFactory mappedClassFactory;
		private MappedClassRegistry mappedClassRegistry;
		private String[] packagesToScan;
		private MappedFieldFactory mappedFieldFactory;
		private MappedFieldBuilderVisitor[] mappedFieldVisitors;
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

		private Builder me() {
			return this;
		}

		/**
		 * Provided alternate storage for registry meta data
		 */
		public Builder resourceRegistryRepository(ResourceRegistryRepository resourceRegistryMap) {
			this.resourceRegistryMap = resourceRegistryMap;
			return me();
		}

		/**
		 * This will behave as a default but will not override settings explicitly
		 * passed elsewhere
		 */
		public Builder packagesToScan(String... packagesToScan) {
			this.packagesToScan = packagesToScan;
			return me();
		}

		/**
		 * Comparator for defining sort order of {@link MappedClass#getAttributes()}
		 */
		public Builder mappedFieldOrderComparator(Comparator<MappedField<?>> mappedFieldOrderComparator) {
			this.mappedFieldOrderComparator = mappedFieldOrderComparator;
			return me();
		}

		/**
		 * Provide an alternate implementation for creating {@link MappedField}
		 */
		public Builder mappedFieldFactory(MappedFieldFactory mappedFieldFactory) {
			this.mappedFieldFactory = mappedFieldFactory;
			return me();
		}

		/**
		 * If {@link #mappedFieldFactory(MappedFieldFactory)} is not overridden,
		 * {@link MappedFieldBuilderVisitor} implementations may be specified to
		 * customize behavior of {@link DefaultMappedFieldFactory}
		 */
		public Builder mappedFieldBuilderVisitors(MappedFieldBuilderVisitor... visitors) {
			this.mappedFieldVisitors = visitors;
			return me();
		}

		/**
		 * Overrides factory for providing default
		 * {@link com.github.restup.repository.Repository} implementations.
		 */
		public Builder repositoryFactory(RepositoryFactory repositoryFactory) {
			this.repositoryFactory = repositoryFactory;
			return me();
		}

		/**
		 * Overrides factory for providing error objects
		 */
		public Builder errorFactory(ErrorFactory errorFactory) {
			this.errorFactory = errorFactory;
			return me();
		}

		/**
		 * @param methodArgumentFactory
		 * @return
		 */
		public Builder methodArgumentFactory(MethodArgumentFactory methodArgumentFactory) {
			this.methodArgumentFactory = methodArgumentFactory;
			return me();
		}

		/**
		 * Defines default service method access for resources. Resources may define
		 * their own.
		 */
		public Builder serviceMethodAccess(ServiceMethodAccess defaultServiceMethodAccess) {
			this.defaultServiceMethodAccess = defaultServiceMethodAccess;
			return me();
		}

		/**
		 * Defines default service controller access for resources. Resources may define
		 * their own.
		 */
		public Builder controllerMethodAccess(ControllerMethodAccess defaultControllerMethodAccess) {
			this.defaultControllerMethodAccess = defaultControllerMethodAccess;
			return me();
		}

		/**
		 * If set to true default Up! {@link ParameterConverter} implementations will
		 * not be used
		 */
		public Builder excludeDefaultConverters(boolean excludeDefaultConverters) {
			this.excludeDefaultConverters = excludeDefaultConverters;
			return me();
		}

		/**
		 * Overrides default {@link RequestObjectFactory}
		 */
		public Builder requestObjectFactory(RequestObjectFactory requestObjectFactory) {
			this.requestObjectFactory = requestObjectFactory;
			return me();
		}

		/**
		 * Overrides default {@link MappedClassFactory}
		 */
		public Builder mappedClassFactory(MappedClassFactory mappedClassFactory) {
			this.mappedClassFactory = mappedClassFactory;
			return me();
		}

		/**
		 * If true, default filters ({@link NotFoundFilter}, etc) will be excluded from
		 * default filters
		 */
		public Builder excludeFrameworkFilters(boolean excludeFrameworkFilters) {
			this.excludeFrameworkFilters = excludeFrameworkFilters;
			return me();
		}

		/**
		 * Define default service filters to be used for resources relying on filter
		 * based services. This will add to default Up! filters unless,
		 * {@link #excludeFrameworkFilters(boolean)} is set to true
		 */
		public Builder defaultServiceFilters(Object... filters) {
			this.defaultServiceFilters = filters;
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
		 * Default implementation to be used when resource does not specify it's own
		 * implementation
		 */
		public Builder defaultRestrictedFieldsProvider(ResourcePathsProvider restrictedFieldsProvider) {
			this.defaultRestrictedFieldsProvider = restrictedFieldsProvider;
			return me();
		}

		/**
		 * Default implementation to be used when resource does not specify it's own
		 * implementation
		 */
		public Builder defaultSparseFieldsProvider(ResourcePathsProvider defaultSparseFieldsProvider) {
			this.defaultSparseFieldsProvider = defaultSparseFieldsProvider;
			return me();
		}

		/**
		 * The default base path for all resources
		 */
		public Builder basePath(String basePath) {
			this.basePath = basePath;
			return me();
		}

		public RegistrySettings build() {
			String[] packagesToScan = this.packagesToScan;
			if (ArrayUtils.isEmpty(packagesToScan)) {
				packagesToScan = new String[] { "com" };
			}
			Comparator<MappedField<?>> mappedFieldOrderComparator = this.mappedFieldOrderComparator;
			if (mappedFieldOrderComparator == null) {
				mappedFieldOrderComparator = new MappedFieldComparator();
			}
			MappedFieldBuilderVisitor[] mappedFieldVisitors = this.mappedFieldVisitors;
			if (ArrayUtils.isEmpty(mappedFieldVisitors)) {
				List<MappedFieldBuilderVisitor> visitors = new ArrayList<MappedFieldBuilderVisitor>();
				visitors.add(new IdentityByConventionMappedFieldBuilderVisitor());
				if (AutoDetectConstants.JACKSON2_EXISTS) {
					visitors.add(new JacksonMappedFieldBuilderVisitor());
				}
				mappedFieldVisitors = visitors.toArray(new MappedFieldBuilderVisitor[0]);
			}
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

				Validator javaxValidations = this.validator;
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

			ConverterFactory.Builder builder = ConverterFactory.builder();
			if (!excludeDefaultConverters) {
				builder.addDefaults();
			}
			// builder.add(parameterConverters);
			ConverterFactory converterFactory = builder.build();

			ParameterConverterFactory parameterConverterFactory = ParameterConverterFactory
					.builder(errorFactory)
					.addAll(converterFactory.getConverters(String.class))
					.build();

			ControllerMethodAccess defaultControllerMethodAccess = this.defaultControllerMethodAccess;
			if (defaultControllerMethodAccess == null) {
				defaultControllerMethodAccess = ControllerMethodAccess.builder().setAllEnabled().build();
			}

			ServiceMethodAccess defaultServiceMethodAccess = this.defaultServiceMethodAccess;
			if (defaultServiceMethodAccess == null) {
				defaultServiceMethodAccess = ServiceMethodAccess.builder().setAllEnabled().build();
			}

			Pagination pagination = defaultPagination;
			if (pagination == null) {
				pagination = Pagination.of(10, 0);
			}

			ResourcePathsProvider defaultSparseFields = this.defaultSparseFieldsProvider;
			if (defaultSparseFields == null) {
				defaultSparseFields = AllResourcePathsProvider.getDefaultSparseFieldsProvider();
			}

			ResourcePathsProvider restrictedFields = this.defaultRestrictedFieldsProvider;
			if (restrictedFields == null) {
				restrictedFields = new EmptyResourcePathsProvider();
			}

			String basePath = Resource.cleanBasePath(this.basePath);
			if (basePath == null) {
				basePath = "/";
			}

            return new BasicRegistrySettings(resourceRegistryMap, mappedClassFactory, mappedClassRegistry, packagesToScan,
					mappedFieldFactory, mappedFieldVisitors, mappedFieldOrderComparator, defaultControllerMethodAccess,
					defaultServiceMethodAccess, repositoryFactory, errorFactory, requestObjectFactory,
					methodArgumentFactory, converterFactory, parameterConverterFactory, defaultServiceFilters,
					pagination, defaultSparseFields, restrictedFields, basePath);
		}
	}

	static class MappedFieldComparator implements Comparator<MappedField<?>> {

		@Override
        public int compare(MappedField<?> a, MappedField<?> b) {
			if (a == null) {
                return b == null ? 0 : 1;
			}
            if (b == null) {
                return -1;
            }
			if (a.isIdentifier()) {
                if (!b.isIdentifier()) {
                    return -1;
                }
            } else if (b.isIdentifier()) {
				return 1;
			}
            int result = StringUtils.compare(a.getApiName(), b.getApiName());
            if (result == 0) {
                result = StringUtils.compare(a.getBeanName(), b.getBeanName());
			}
            return result;
		}

	}

}
