# REST Up! Framework

[![Build Status](https://travis-ci.org/restup/up-framework.svg?branch=master)](https://travis-ci.org/restup/up-framework)
[![Coverage Status](https://coveralls.io/repos/github/restup/up-framework/badge.svg?branch=coverage)](https://coveralls.io/github/restup/up-framework?branch=coverage)

Up! Framework aims to simplify building and testing RESTful services.

###### Up! Framework:
* Maps API, bean and persistence field names to provide feature rich APIs from annotated objects
* Allows customization using fluent builders for configuration
* Uses consistent resource semantics for all apis, exposing a collection and item resource (unless otherwise configured) 
* Uses consistent conventions for API parameters to support sparse fields requests, sorting, pagination, and filtering of resources
* Is not coupled to other persistence, serialization, or web application frameworks, making your APIs completely portable. 
* Is designed to support PATCH operations correctly and easily
* Provides flexible, filter based services promoting composition and reuse.
* Provides content negotiation to support multiple json response formats (json, json api, or hal)
* Supports [JSR 303 Bean Validation](http://beanvalidation.org/1.0/spec/)
* Supports automatic validation of resource relationships (unless configured otherwise)
* Supports compound documents (json api includes)

###### Up! Test Framework:
* Takes advantage of API consistency for concise and thorough integration style API testing
* Provides fluent builders for testing api requests
* Uses sensible defaults for requests and assertions


## Getting started

Up Framework is still a work in progress and has not yet been released.

Check out the [Spring Boot Demo App](https://github.com/restup/up-framework-demo/tree/master/up-demo-spring-boot)

This documentation is also a work in progress

## ResourceRegistry

To build APIs using the Up! Framework, a ResourceRegistry must be defined.

The ResourceRegistry stores all of the meta data for registered resources including:
* classes registered as resources
* field meta data for all API objects
* services registered by resource
* repositories registered by resource
* relationships between resources

RegistrySettings are used to configure a ResourceRegistry, which contain sensible defaults for
configurations and implementations.  Up! Framework aims to be completely configurable and extensible.

When defining a ResourceRegistry, at a minimum, you may want to define a RepositoryFactory.  
A RespositoryFactory can provide default ResourceRepository implementations required by registered 
resources.  A RepositoryFactory is not required, but if not defined each resource must configure
it's own  service or repository.  Of course, if a service or repository is configured for a resource, 
it will be used regardless of the RepostoryFactory configuration.

```java
ResourceRegistry registry = new ResourceRegistry(RegistrySettings.builder()
                .setRepositoryFactory(new JpaRepositoryFactory(jpaRepository)));
```

##### RegistrySettings Configurations

<b>*</b> Denotes a registry setting providing resource defaults.

###### PackagesToScan

Indicates the packages that are included when scanning API objects for metadata.
Defaulted to ["com"].

###### MappedFieldOrderComparator

Defines the order that field meta data is maintained in the ResourceRegistry.  By default
fields are ordered naturally by apiName once during meta data dicscovery. 

The side effect of this is that fields are serialized in this order by default.  
While JSON is not required to guarantee order, consistency is nice.  

###### MappedClassFactory

MappedClassFactory is used by the ResourceRegistry to discover class level meta data

###### MappedFieldFactory

The MappedFieldFactory is used by the DefaultMappedClassFactory to discover field level meta data

_**If a custom MappedClassFactory is used, configuring MappedFieldFactory would have no effect.**_

###### MappedFieldBuilderVisitors

The DefaultMappedFieldFactory will apply visitors implementing MappedFieldBuilderVisitor when 
discovering field meta data.  It is in this way that API and persistence annotations are discovered 
and override the default names.

_**If a custom MappedClassFactory or MappedFieldFactory is used, configuring MappedFieldBuilderVisitors would have no effect.**_

###### <b>*</b> RepositoryFactory

A RepositoryFactory is an optional, but convenient way of providing default repository implementations for registered resources

###### ErrorFactory

ErrorFactory instantiates the error objects used by Up! allowing customization if needed.

###### RequestObjectFactory

RequestObjectFactory provides all of the request object instances used by Up! allowing customization if needed.

###### MethodArgumentFactory

For filter based services (see below), the MethodArgumentFactory instantiates arguments of the
filter methods.

###### ParameterConverters

Add Converters used by Up! framework for type conversion, applicable when parsing http parameters.

This will _add_ converters or _override_ a default converter targeting the same type. However, default converters
are still added. see below to exclude

###### ExcludeDefaultParameterConverters

Excludes all Up! default parameter converters

###### Validator

The javax.validation.Validator used by Up!.  Auto detected and used if present by default.
A Validator must be added to the classpath to enable validations.
ExcludeFrameworkFilters will also disable validations, regardless of Validator configuration

###### <b>*</b> ServiceMethodAccess

Defines which service methods (internally) are enabled 

###### <b>*</b> ControllerMethodAccess 
Defines which service methods are exposed by the controller by default.
For example, allows exposing only read only API by default.

###### <b>*</b> DefaultServiceFilters 
Specifies services filters that should be used by default for all filter based 
services.  

Registering an implementation of ServiceFilter provides a convenient way to
add behavior across all or specific types of resources.

###### ExcludeFrameworkFilters

Setting to true will disable all default Up! service filters, impacting out of the box functionality.

TODO describe filters

###### <b>*</b> DefaultPagination

Specifies the default pagination settings used by resources.

|Pagination Property|Description|
|---|---|
|limit|The max limit that may be used|
|pagingDisabled| disables paging (all results are returned)|
|withTotalsDisabled| total count is not queried, disabling last page support|

###### <b>*</b> DefaultRestrictedFieldsProvider

Provides a means to restrict fields from being serialized, perhaps 
offering a means to define access control over fields serialized in API responses.

###### <b>*</b> DefaultSparseFieldsProvider

Specifies the default fields to be serialized in responses.  By default, all non transient
api fields are included.

###### <b>*</b> BasePath

Specifies the basePath of the resource in the API.  Default is "/"
 
## Resources

Up! is designed around Resource definitions.  Resource may be any POJO
and minimally require a Service to be defined.

* Any POJO can be defined as a resource.
* A resource must define either a Service or a Repository (see below)
* A resource must have an identifier

```java
Resource resource = Resource.builder(University.class)
                .setName("university")
                .setPluralName("universities").build();
```

Up! annotation may also be used to define name (@ApiName) and pluralName (@Plural)

##### Resource Configurations

###### name
The resourceName
Can be specified using a Resource.Builder, but perhaps more conveniently done using annotations

```java
@ApiName("university")
public class University 
```

###### pluralName
The plural resourceName used for collection endpoints.  By default appends 's' to apiName.
Can be specified using a Resource.Builder, but perhaps more conveniently done using annotations

```java
@Plural("universities")
public class University 
```

###### basePath
The base url path for a resource, defaulted by RegistrySettings.

###### ControllerAccess
API access for a resource, defaulted by RegistrySettings

###### ServiceAccess
Internal service access for a resource, defaulted by RegistrySettings

###### Repository
A repository for persistence operations.  See below for more info on repositories.
A repostiory is optional _if_ a service is provided.
If neither a service or repository is specified a default may be provided by RegistrySettings.
An exception will be thrown if a repository is not available.

###### Service
The internal service for a resource. By default a filter based service is used

###### ServiceFilters
When using filter based service, adds resource specific service filters.

###### ExcludeDefaultServiceFilters
By default, all RegistrySettings service filters are added to all resource.  Setting
to false will disable this behavior

###### DefaultPagination
Sets the default pagination rules for a resource, defaulted by RegistrySettings

###### RestrictedFieldsProvider
Restricts serialized fields for a resource, defaulted by RegistrySettings

###### SparseFieldsDefaultsProvider
Provides default fields to serialize for a resource, defaulted by RegistrySettings

This allows setting the default fields to be a subset of all fields, forcing usage of sparse field requests. 

## Methods without signatures

Before discussing services and repositories, flexible method signatures used by Up! must be understood.

Up! uses method annotations to tag methods providing functionality.  This is very similar to common web
application frameworks using annotations to map endpoints.  The actual signatures of the methods do 
not matter.  Up! takes this convenience and provides it throughout a service implementation.

Just as in other web application frameworks there are some common arguments that can be used in the context
of the request

|Argument| Description|
|---|---|
|Resource| the current resource requested|
|ResourceRegistry| the registry of the resource|
|Errors|An error builder to append any request errors to|
|ResourceRequest|Represents the request received.|
|[resource class]| for persistence operations, the resource instance passed in the request|
|ResourcePath| for validation methods, the path of the request validated|

Any Interface matched to an object in the request context can be used.
For example, ReadRequest, ListRequest, DeleteRequest, UpdateRequest and so on extend
ResourceRequest. Any may be used as method args for their respective operations.

Additionally, **any pojo** may be used as a method argument, providing it has a no arg constructor.
Pojo arguments will be instantiated and used throughout the request. _**this can be used to carry state
across filter methods for a request**_

Pojo arguments may also use @Param annotations.  If present, request parameters will be automatically bound
to the pojo.  This can be a convenient way of adding additional API parameters.

## Services

A service can be any java object and must either implement ResourceService or use Up! annotations (see Annotations for more info) 

It is not required to implement all methods to be a service, however
absence of support for an operation will prevent that operation from being exposed in the API
regardless of ControllerMethodAccess settings.  For example, if create methods are not defined, POST operations would 
not be exposed in the resulting API.

By default, if a repository is configured, a filter based service will be used.  

### Filter Based Services

Persistence operations are often very redundant - often there is common logic to persist objects, 
common logic prior to persistence and common logic after persistence.

Filter based services provide a means to wire in annotated filter methods before and after persistence operations.
Annotated filter methods have no defined signature as previously defined.
 
Service filters may be common to all resources when added to RepositorySettings.

Service filters may be common to select resource when added to RepositorySettings and implementing
ServiceFilter.

Service filters may be specific to a resource when configured for a specific resource.

#### Service Filter Tag Annotations

The following annotations simply tag method as hanlding pre/post functionality

|Annotation|Description|
|---|---|
|@PreCreateFilter| Method will execute prior to @CreateResource operations |
|@PreReadFilter  | Method will execute prior to @ReadResource operations |
|@PreUpdateFilter| Method will execute prior to @UpdateResource operations |
|@PreDeleteFilter| Method will execute prior to @DeleteResource operations |
|@PreListFilter  | Method will execute prior to @ListResource operations |
|@PostCreateFilter| Method will execute following @CreateResource operations |
|@PostReadFilter  | Method will execute following @ReadResource operations |
|@PostUpdateFilter| Method will execute following @UpdateResource operations |
|@PostDeleteFilter| Method will execute following @DeleteResource operations |
|@PostListFilter  | Method will execute following @ListResource operations |

#### Correctly Implementing PATCH with @Validation

PATCH operations are deceptively tricky.

For one, it is not possible to correctly implement PATCH handling passed null values using default
deserialization with common web application frameworks. The JSON from both examples below
will result in the foo field of a deserialized Pojo being null - It is not possible using this default
serialization to detect whether a request intends to set foo to null. 

```java
{ "data" : { "foo" : null } }
```

```java
{ "data" : { "bar" : 123 } }
```

Additionally, all of the state required for a validation may not be present in the request.
It is therefore necessary to merge existing persisted state and request state to accurately perform
some validations.  The code for detecting and merging this state increases in complexity as more
fields are needed for a validation.

Up! is designed to handle PATCH operations and address these problems.

Create and Update operations use custom deserialization to parse request documents with available 
parser library objects directly (such as, but not necessarily Jackson).  This custom deserialization
captures the paths requested, so that "foo" can be correctly updated as null in the above example.

Additionally, when using @Validation, persisted state will automatically be merged to request state for
validations.  For example, below if _"foo"_ is passed in the request, _"bar"_ and _"baz"_ from persisted state
will be populated to _fooBar_ so that the validateFooAndBar can apply validation of the 3 fields correctly. 

```
   @Validation(path = {"foo", "bar", "baz"})
   public void validateFooAndBar(Errors errors, ResourcePath path, FooBar fooBar) 
```


## Repositories

TODO

#### Repository Tag Annotations

Repositories and Services use the same annotations for their operations.  The following
Annotations simply tag methods as providing the implementation described.

|Annotation|Description|
|---|---|
|@CreateResource| Method will create a resource |
|@ReadResource| Method will find a resource by id |
|@UpdateResource| Method will Update a resource by id|
|@DeleteResource| Method will Delete a resource by id|
|@ListResource| Method will list resources |


## Controller

TODO


### Annotations
