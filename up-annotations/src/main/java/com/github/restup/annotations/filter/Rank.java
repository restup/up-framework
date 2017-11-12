package com.github.restup.annotations.filter;

import java.lang.annotation.*;

/**
 * Annotation to define order of method execution
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Rank {

    /**
     * Methods ranked lower will execute first
     *
     * @return
     */
    int value();

}
