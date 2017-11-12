package com.github.restup.annotations.field;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotation for defining a field which may be bound to request parameters
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RUNTIME)
@Documented
public @interface Param {

    /**
     * The parameter names whose values may be bound to the field annotated using this annotation
     *
     * @return
     */
    String[] value() default {};

}
