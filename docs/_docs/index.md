---
title: Welcome
permalink: /docs/home/
redirect_from: /docs/index.html
---

Web application frameworks support RESTful APIs to varying degrees often leading to a significant amount of boiler plate code and becoming coupled to both the framework and a specific media type (HAL, JsonAPI, etc).  Up! Framework avoids this, leaving you with a portable and flexible API implementation.

#### Up! Framework:
* Maps API, bean and persistence field names stored in a ResourceRegistry
* Allows customization using fluent configuration builders
* Uses consistent resource semantics for all apis, exposing a collection and item resource with a single mapping.
* Uses consistent conventions for API parameters to support sparse fields requests, sorting, pagination, and filtering of resources
* Is not coupled to other persistence, serialization, or web application frameworks, making your APIs completely portable. 
* Is designed to support [PATCH]({{site.baseurl}}/docs/patch) operations correctly and easily
* Supports flexible, [filter based services]({{site.baseurl}}/docs/services/#filter-based-services) promoting composition and reuse.
* Provides content negotiation to support multiple json response formats (json, json api, or hal)
* Supports [JSR 303 Bean Validation](http://beanvalidation.org/1.0/spec/)
* Supports automatic validation of resource relationships (unless configured otherwise)
* Supports compound documents (json api includes)

####	 Up! Test Framework:
* Takes advantage of API consistency for concise and thorough integration style API testing
* Provides fluent builders for testing api requests
* Uses sensible defaults for requests and assertions
