---
title: Methods without signatures
permalink: /docs/methodsWithoutSignatures/
---

Up! uses method annotations to tag methods providing functionality.  This is very similar to common web application frameworks using annotations to map endpoints.  The actual signatures of the methods do not matter.  Up! takes this convenience and provides it throughout a service implementation.  

Just as in other web application frameworks there are some common arguments that can be used in the context of the request

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
Pojo arguments will be instantiated and used throughout the request. 

Pojo arguments can be used to carry state across filter methods for a request.
{: .note .info }

Pojo arguments may also use @Param field annotations.  If present, request parameters will be automatically bound
to the pojo.  This can be a convenient way of adding additional API parameters.