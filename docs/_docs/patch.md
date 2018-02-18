---
title: PATCH 
permalink: /docs/patch/
---

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

