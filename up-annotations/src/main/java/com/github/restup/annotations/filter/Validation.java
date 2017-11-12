package com.github.restup.annotations.filter;

import java.lang.annotation.*;

/**
 * Annotates a validation method
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Validation {

    /**
     * Path(s) for which the validation will apply
     *
     * @return
     */
    String[] path();

    /**
     * On create, indicates whether the validation must execute if the path is absent.
     * For example if required is false and the path is absent, the validation may be skipped.
     * On the other hand, if true and the path is absent, the validate must still execute.
     *
     * @return
     */
    boolean required() default true;

    /**
     * If true, the method will execute on create. Otherwise will not execute on create.
     *
     * @return
     */
    boolean onCreate() default true;

    /**
     * If true, the method will execute on update. Otherwise will not execute on update.
     *
     * @return
     */
    boolean onUpdate() default true;

    /**
     * If Errors exist for the request and true, the validation may be skipped.  If false,
     * the validation will execute regardless of errors.  If a validation incurs expensive
     * or time consuming processing it may be useful to set skipOnErrors to true.
     *
     * @return
     */
    boolean skipOnErrors() default false;

}
