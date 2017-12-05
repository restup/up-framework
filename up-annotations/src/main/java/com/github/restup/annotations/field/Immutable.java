package com.github.restup.annotations.field;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Annotation for defining immutable fields
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RUNTIME)
@Documented
public @interface Immutable {

    /**
     * if true, the field is immutable and may not be updated
     */
    boolean value() default true;

    /**
     * if true, updates attempts will respond with an error. Otherwise update attempts are to be ignored
     */
    boolean errorOnUpdateAttempt() default false;

}
