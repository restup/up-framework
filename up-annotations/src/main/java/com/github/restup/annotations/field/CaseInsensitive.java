package com.github.restup.annotations.field;

import java.lang.annotation.*;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * An {@link Annotation} to define a field that should be treated as case
 * insensitive when used as a query filter
 *
 * @author abuttaro
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RUNTIME)
@Documented
public @interface CaseInsensitive {

    /**
     * True indicates that the field should be treated as case insensitive for
     * queries.
     *
     * @return
     */
    boolean value() default true;

    /**
     * For case insensitive field, read operations may need to use an alternate
     * field (lower/upper cased). This may define such a field if needed. The field
     * should also exist on the same type.
     *
     * @return
     */
    String searchField() default "";

    /**
     * Indicates whether lower case or upper cased search field is used.
     *
     * @return
     */
    boolean lowerCased() default true;

}
