---
title: Test Support
permalink: /docs/test/
---

Testing should be as quick, concise and thorough as possible.  

Up! Framework API testing uses the builder pattern to build test cases which make API requests and assertions using defaults and test context to be as concise as possible while still being extremely thorough.

The [RestApiAssertions.Builder]({{site.baseurl}}/docs/restApiAssertions) takes advantage of consistent RESTful semantics followed by Up! Framework for very concise testing.  However, not all APIs truly follow RESTful semantics.  For RPC style APIs, an [RpcApiAssertions.Builder]({{site.baseurl}}/docs/rpcApiAssertions) still uses sensible defaults for API testing.

#### APIExecutors

Both API test builders require an `com.github.restup.test.ApiExecutor` instance to execute an API request for the request.  This allows the test builders to test using a mock request, a mock test framework (such as Spring MockMVC or Play! FakeRequest), or make an actual request.

APIExecutors:
- `com.github.restup.test.spring.MockMVCApiExecutor`
- `com.github.restup.controller.mock.MockApiExecutor`

#### Relative Resources

By default, Up! test builders will use relative paths to load content from the classpath for requests and response comparison.  However, while typically a bit more cumbersome, it is also possible to pass content for requests and responses as a String or byte[] directly to the builder.  

The base path of resource will match that of the test class.  The test class can be passed explicitly to the test builder or the builder will discover the class automatically from the Threads stack trace.  

Request bodies for POST, PUT, and PATCH are read from a `requests` directory in the base path.

Response bodies for POST, PUT, and PATCH are read from a `responses` directory in the base path.  The builders use hamcrest [JsonUnit](https://github.com/lukas-krecan/JsonUnit) matchers to assert that the response contents match the response body.  [JsonUnit](https://github.com/lukas-krecan/JsonUnit) supports regular expressions, placeholders and more for matching responses.