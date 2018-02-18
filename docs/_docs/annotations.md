---
title: Annotations
permalink: /docs/annotations/
---

#### Operation Tag Annotations

Repositories and Services use the same annotations to define their operations.  

|Annotation|Description|
|---|---|
|@CreateResource| Method will create a resource |
|@ReadResource| Method will find a resource by id |
|@UpdateResource| Method will Update a resource by id|
|@DeleteResource| Method will Delete a resource by id|
|@ListResource| Method will list resources |

Use the Annotations to simply tag methods as providing the functionality implied:

```java
@CreateResource
PersistenceResult<T> create(CreateRequest<T> request) {
  ...
}
```


#### Service Filter Tag Annotations

The following annotations simply tag method as handling pre/post functionality

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