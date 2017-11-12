package com.github.restup.annotations;

import java.lang.annotation.*;

/**
 * Annotation to provide pluralization of a type or field
 *
 * @author andy.buttaro
 */
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Plural {

    String value();

}
