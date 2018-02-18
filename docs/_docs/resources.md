---
title: Resources
permalink: /docs/resources/
---

Up! is designed around Resource definitions and Resource meta-data.  

* Any POJO can be defined as a resource.
* POJO-less (Map backed) Resources are also supported using Resource.Builder to define fields and types.
* A resource must define either a [Service]({{site.baseurl}}/docs/services/) or a [Repository]({{site.baseurl}}/docs/repositories/).
* A resource must have an identifier

Resources can be defined simply if using Registry defaults:

```java
resourceRegistry.registerResource(University.class, Course.class);
```

Or using a resource builder for more control or customization:

```java
Resource resource = Resource.builder(University.class)
                .setName("university")
                .setPluralName("universities").build();
```

Up! annotation may also be used to define name (@ApiName) and pluralName (@Plural)

## Settings

<b>*</b> Denotes that the value is defaulted to the value defined in Registry settings.
{: .note .info }

#### name
The resourceName

Can be specified using a Resource.Builder

```java
.name("university");
```

but perhaps more conveniently done using annotations

```java
@ApiName("university")
public class University 
```


#### pluralName
The plural resourceName used for collection endpoints. By default appends 's' to apiName.

Can be specified using a Resource.Builder

```java
.pluralName("universities");
```
but perhaps more conveniently done using annotations.

```java
@Plural("universities")
public class University 
```


#### * basePath
The base url path for a resource.

```java
.basePath("universities");
```

#### * ControllerMethodAccess
API access for a resource

```java
.controllerMethodAccess(ControllerMethodAccess.allEnabled())
```

#### * DefaultPagination
Sets the default pagination rules for a resource

```java
.defaultPagination(Pagination.disabled())
```

#### * ExcludeFrameworkFilters

Up! provides default service filters to apply to filter based services in addition to any default service filters configured above. Setting to true will disable all default Up! service filters, impacting out of the box functionality.

```java
.excludeFrameworkFilters(false)
```

#### * Repository
A repository for persistence operations. 

A repository is optional _if_ a service is provided. The default filter based service requires a repository.

If neither a service or repository is specified a default repository may be provided using a RepositoryFactory configured in Registry Settings.

An exception will be thrown if a repository is not available.

See [Repositories]({{site.baseurl}}/docs/repositories/) for more detail.

```java
.repository(instance)
```

#### * RestrictedFieldsProvider
Restricts serialized fields for a resource, defaulted by RegistrySettings

```java
.restrictedFieldsProvider(ResourcePathsProvider.empty())
```

#### Service
The internal service for a resource. By default a filter based service is used.

```java
.service(instance)
```

See [Services]({{site.baseurl}}/docs/services/) for more detail.

#### * ServiceMethodAccess
Internal service access for a resource.

```java
.serviceMethodAccess(ServiceMethodAccess.allEnabled())
```

#### * ServiceFilters
When using filter based service, adds resource specific service filters.

```java
.serviceFilters(Object...)
```
#### * SparseFieldsDefaultsProvider
Provides default fields to serialize for a resource

```java
.sparseFieldsProvider(ResourcePathsProvider.allApiFields())
```

This allows setting the default fields to be a subset of all fields, forcing usage of sparse field requests. 
{: .note .info }
