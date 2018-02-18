---
title: Services
permalink: /docs/services/
---

A service can be any java object and must either implement `com.github.restup.service.ResourceService` or use [Up! annotations]({{site.baseurl}}/docs/annotations/#operation-tag-annotations)

It is not required to implement all methods to be a service, however absence of support for an operation will prevent that operation from being exposed in the API regardless of `ControllerMethodAccess` settings.  For example, if create methods are not defined, POST operations would not be exposed in the resulting API.

By default, if a repository is configured, a filter based service will be used.  

### Filter Based Services

Persistence operations are often very redundant and services that expose these operations typically have common persistence logic and common logic prior to and after each persistence operation.  

Filter based services execute a list of methods annotated by [pre and post annotations]({{site.baseurl}}/docs/annotations/#service-filter-tag-annotations) before and after the persistence operation defined by its [repository]({{site.baseurl}}/docs/repositories)

Service filters can be defined globally in [registry settings]({{site.baseurl}}/docs/resourceRegistry/#-defaultservicefilters).

Service filters can be defined specifically for a resource [resource]({{site.baseurl}}/docs/resources/#-servicefilters)).

If a service filter object implements `com.github.restup.service.ServiceFilter` the filter method(s) of that object will only apply if the ServiceFilter `accepts` the resource.

Using a globally defined ServiceFilter accepting only resources based upon interfaces or common inheritance offers a powerful means to compose services.
{: .note }  

As filter methods are simply annotated, there is not a defined signature for filter methods.  See [Methods Without Signatures]({{site.baseurl}}/docs/methodsWithoutSignatures) for more information.
 

