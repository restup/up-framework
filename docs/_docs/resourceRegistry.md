---
title: Resource Registry
permalink: /docs/resourceRegistry/
---

To build APIs using the Up! Framework, a ResourceRegistry must be defined.

```java
ResourceRegistry registry = ResourceRegistry.builder().build();
```

The ResourceRegistry stores all of the meta data for registered resources including:
* classes registered as resources
* field meta data for all API objects
* services registered for resources
* repositories registered for resources
* relationships between resources

The ResourceRegistry is fully configurable, but contains sensible defaults for required configurations and implementations. 

When defining a ResourceRegistry, at a minimum, you may want to define a RepositoryFactory to provide a default repository implementation. 
{: .note .info }

## Settings

Custom implementations may be configured here. For example, a ThreadLocal SecurityContext may provide UserDetails which may be used to determine configurations used at runtime. 
{: .note }

<b>*</b> Denotes a registry setting which provides Resource setting defaults.
{: .note .info }

#### <b>*</b> BasePath

Specifies the basePath of the resource in the API.  Default is "/"

```java
.basePath("/")
```

#### <b>*</b> ControllerMethodAccess 
Defines which service methods are exposed by the controller by default.
For example, allows exposing only read only API by default. All are enabled by default.

```java
.controllerMethodAccess(ControllerMethodAccess.allEnabled())
```

#### ConverterFactory

Factory for parameter conversion.

```java
.converterFactory(ConverterFactory.builder().addDefaults().build());
```

#### <b>*</b> DefaultPagination

Specifies the default pagination settings used by resources.

```java
.defaultPagination(implementation) 
```

#### <b>*</b> DefaultRestrictedFieldsProvider

Provides a means to restrict fields from being serialized, perhaps 
offering a means to define access control over fields serialized in API responses.

```java
.defaultRestrictedFieldsProvider(ResourcePathsProvider.empty())
```

#### <b>*</b> DefaultServiceFilters 
Specifies services filters that should be used by default for all filter based 
services.  

```java
.defaultServiceFilters(Object...)
```

Registering an implementation of ServiceFilter provides a convenient way to
add behavior across all or specific types of resources. Filters which implement com.github.restup.service.ServiceFilter will only apply to resources accepted by the filter.  

com.github.restup.service.ServiceFilter can be used to apply a filter to a set of objects implementing the same interface or with common inheritance
{: .note }

#### <b>*</b> DefaultSparseFieldsProvider

Specifies the default fields to be serialized in responses.  By default, all non transient
api fields are included.


```java
.defaultSparseFieldsProvider(ResourcePathsProvider.allApiFields())
```

#### ErrorFactory

ErrorFactory instantiates the error objects used by Up! allowing customization if needed.


```java
.errorFactory(ErrorFactory.getDefaultErrorFactory())
```

#### ExcludeDefaultParameterConverters

Excludes all Up! default parameter converters.  Parameter converters convert request parameters to appropriate types based on object mapping.

```java
.excludeDefaultConverters(false)
```

#### ExcludeFrameworkFilters

Up! provides default service filters to apply to filter based services in addition to any default service filters configured above. Setting to true will disable all default Up! service filters, impacting out of the box functionality.

```java
.excludeFrameworkFilters(false)
```

#### MappedClassFactory

MappedClassFactory is used by the ResourceRegistry to discover class level meta data

```java
.mappedClassFactory(mappedClassFactory)
```

#### MappedFieldFactory

The MappedFieldFactory is used by the DefaultMappedClassFactory to discover field level meta data

```java
.mappedFieldFactory(mappedFieldFactory)
```

If a custom MappedClassFactory is used, configuring MappedFieldFactory would have no effect.
{: .note .warning }

#### MappedFieldBuilderVisitors

MappedFieldBuilderVisitor is used by DefaultMappedFieldFactory to discover third party annotated fields such as Jackson.

```java
.mappedFieldBuilderVisitors(MappedFieldBuilderVisitor... visitors)
```

Up! will add MappedFieldBuilderVisitors automatically when possible.
For example, Jackson annotations will be detected automatically
{: .note .info }

If a custom MappedClassFactory or MappedFieldFactory is used, configuring MappedFieldBuilderVisitors would have no effect.
{: .note .warning }


#### MappedFieldOrderComparator

Defines the order that field meta data is maintained in the ResourceRegistry.  By default fields are ordered naturally by apiName once during meta data discovery. 

The side effect of this is that fields are serialized in this order by default.  

While JSON is not required to guarantee order, consistency is nice.  

```java
.mappedFieldOrderComparator(Comparator<MappedField<?>>)
```

#### MethodArgumentFactory

For filter based services (see below), the MethodArgumentFactory instantiates arguments of the filter methods.

```java
.methodArgumentFactory(MethodArgumentFactory)
```

#### PackagesToScan

Indicates the packages that are included when scanning API objects for metadata. Defaulted to ["com"].

```java
.packagesToScan("com")
```

###### Validator

The javax.validation.Validator used by Up!.  Auto detected and used if present by default.
A Validator must be added to the classpath to enable validations.
ExcludeFrameworkFilters will also disable validations, regardless of Validator configuration

```java
.validator(Validator)
```

#### <b>*</b> RespositoryFactory

A RespositoryFactory can provide a default repository implementation for registered resources.  A RepositoryFactory is not required, but if not defined each resource must configure it's own service or repository.  Of course, if a service or repository is configured for a resource, 
it will be used regardless of the RepostoryFactory configuration.

```java
.repositoryFactory(new JpaRepositoryFactory(jpaRepository));
```

#### RequestObjectFactory

RequestObjectFactory provides all of the request object instances used by Up! allowing customization if needed.

```java
.requestObjectFactory(RequestObjectFactory.getDefaultRequestObjectFactory());
```

#### Registry Repository

The registry repository stores resource meta data internally, by default in memory in a Map.  This can be customized if desired.

```java
.resourceRegistryRepository(ResourceRegistryRepository);
```
#### <b>*</b> ServiceMethodAccess

Defines which service methods (internally) are enabled 

```java
.serviceMethodAccess(ServiceMethodAccess.allEnabled());
```


