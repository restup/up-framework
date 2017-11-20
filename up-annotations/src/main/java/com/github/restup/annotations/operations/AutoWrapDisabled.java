package com.github.restup.annotations.operations;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AutoWrapDisabled {

    /**
     * If auto wrap is disabled, the return type will
     * not be automatically be wrapped in Up! response types.
     * @return
     */
    boolean value() default true;

}
