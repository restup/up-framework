package com.github.restup.annotations;

import java.lang.annotation.*;

/**
 * Annotation to define API names for objects
 *
 * @author andy.buttaro
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ApiName {

    /**
     * The API name for the type
     *
     * @return
     */
    String value();

}
