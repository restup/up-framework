package com.github.restup.annotations.operations;

import java.lang.annotation.*;

/**
 * Annotation for a service or repository method which supports deleting a resource
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DeleteResource {

}