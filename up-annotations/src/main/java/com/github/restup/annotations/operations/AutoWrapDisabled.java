package com.github.restup.annotations.operations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AutoWrapDisabled {

    /**
     * If auto wrap is disabled, the return type will not be automatically be wrapped in Up! response types.
     */
    boolean value() default true;

}
