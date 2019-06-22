package com.github.restup.annotations.field;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Annotation for defining an identifier field which permits client generated values
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RUNTIME)
@Documented
public @interface ClientGeneratedIdentifier {

    /**
     * Whether client generated values are allowed (true) or not (false)
     */
    boolean allowed() default true;

}
