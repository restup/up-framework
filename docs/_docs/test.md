---
title: Test Support
permalink: /docs/test/
---

Testing should be as quick, concise and thorough as possible.  

Up! Framework API testing uses the builder pattern to build test cases which make API requests and assertions using defaults and test context to be as concise as possible while still being extremely thorough.

The [RestApiAssertions.Builder]({{site.baseurl}}/docs/restApiAssertions) takes advantage of consistent RESTful semantics followed by Up! Framework for very concise testing.  However, not all APIs truly follow RESTful semantics.  For RPC style APIs, an [RpcApiAssertions.Builder]({{site.baseurl}}/docs/rpcApiAssertions) still uses sensible defaults for API testing.

Both API test builders require an `com.github.restup.test.ApiExecutor` instance to execute an API request for the request.  This allows the test builders to test using a mock request, a mock test framework (such as Spring MockMVC or Play! FakeRequest), or make an actual request.

APIExecutors:
- `com.github.restup.test.spring.MockMVCApiExecutor`
- `com.github.restup.controller.mock.MockApiExecutor`