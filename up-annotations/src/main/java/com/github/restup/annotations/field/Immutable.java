package com.github.restup.annotations.field;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotation for defining immutable fields
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RUNTIME)
@Documented
public @interface Immutable {

    /**
     * if true, the field is immutable and may not be updated
     *
     * @return
     */
    boolean value() default true;

    /**
     * if true, updates attempts will respond with an error. Otherwise update attempts are to be ignored
     *
     * @return
     */
    boolean errorOnUpdateAttempt() default false;

}
