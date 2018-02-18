---
title: Repositories
permalink: /docs/repositories/
---

Similar to services, a Repository can be any java object and must either implement `com.github.restup.repository.ResourceRepository` or use [Up! annotations]({{site.baseurl}}/docs/annotations/#operation-tag-annotations)

All repository implementations are not required to be implemented, however absence of support for an operation will prevent that operation from being exposed in the API regardless of `ControllerMethodAccess` settings.  For example, if create methods are not defined, POST operations would not be exposed in the resulting API.

Current implementations:
- com.github.restup.repository.jpa.JpaRepository
- com.github.restup.repository.collections.MapBackedRepository