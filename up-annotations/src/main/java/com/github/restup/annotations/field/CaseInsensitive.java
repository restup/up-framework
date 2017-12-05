package com.github.restup.annotations.field;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * An {@link Annotation} to define a field that should be treated as case insensitive when used as a query filter
 *
 * @author abuttaro
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RUNTIME)
@Documented
public @interface CaseInsensitive {

    /**
     * True indicates that the field should be treated as case insensitive for queries.
     */
    boolean value() default true;

    /**
     * For case insensitive field, read operations may need to use an alternate field (lower/upper cased). This may define such a field if needed. The field should also exist on the same type.
     */
    String searchField() default "";

    /**
     * Indicates whether lower case or upper cased search field is used.
     */
    boolean lowerCased() default true;

}
