package com.github.restup.annotations.operations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for a service or repository method which supports listing resources <p> Unless ListResource methods are also annotated with {@link AutoWrapDisabled} results will be automatically wrapped by ReadResult. <ul> <li>A ReadResult containing a List of resource objects</li> <li>A List or Collection of resource objects (auto wrapped as ReadResult</li> <li>A resource object (auto wrapped as a ReadResult containing a List of the returned object)</li> </ul>
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ListResource {

}