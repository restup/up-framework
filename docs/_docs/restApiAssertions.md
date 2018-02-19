---
title: RestApiAssertions
permalink: /docs/restApiAssertions/
---

To get started with RestApiAssertions, you must create a builder, passing an apiExecutor, class to use for relative resources, path, and default path args.

```java
Builder api = RestApiAssertions.builder(apiExecutor, getClass(), "/api/students", 1);
```

In the above example, assertions will use /api/students for collection end point requests and /api/students/1 by default for item requests.  Alternate item ids can of course be passed.

The [AbstractMockMVCTest](https://github.com/restup/up-framework/blob/master/up-test-spring/src/main/java/com/github/restup/test/spring/AbstractMockMVCTest.java) provides an example and base test class for using Spring MockMvc.

With the builder you can now make requests fluently.  


#### Examples

The following examples assume `RestApiAssertions.Builder api` has been set up in @Before method in an examples.CourseTest.java test.

###### GET example

The following example will make a request to get course with id 2 and assert that the result matches the content of `/src/test/resources/examples/CourseTest/responses/getCourse.json`

```java
public void getCourse() {
   api.get(2).ok();
}
```

###### POST example

The following example will make a request to add a course using the request body from `/src/test/resources/examples/CourseTest/requests/addCourse.json` and assert that the result matches the content of `/src/test/resources/examples/CourseTest/responses/addCourse.json`

```java
public void addCourse() {
   api.add().ok();
}
```

###### DELETE example

The following example will make a request to get course with id 2 and assert that the result matches the content of `/src/test/resources/examples/CourseTest/responses/testDelete.json`

```java
public void testDelete() {
   api.delete(3).ok();
}
```

###### Additional examples

Hopefully you get the gist of how concise and thorough Up! test assertions can be.

Additional examples can be found here:
- [Spring Boot Demo App](https://github.com/restup/up-framework-demo/blob/master/up-demo-spring-boot/src/test/java/com/github/restup/example/CourseServiceTest.java)