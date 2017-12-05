package com.github.restup.annotations.filter;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotates a validation method
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Validation {

    /**
     * Path(s) for which the validation will apply
     */
    String[] path();

    /**
     * On create, indicates whether the validation must execute if the path is absent. For example if required is false and the path is absent, the validation may be skipped. On the other hand, if true and the path is absent, the validate must still execute.
     */
    boolean required() default true;

    /**
     * If true, the method will execute on create. Otherwise will not execute on create.
     */
    boolean onCreate() default true;

    /**
     * If true, the method will execute on update. Otherwise will not execute on update.
     */
    boolean onUpdate() default true;

    /**
     * If Errors exist for the request and true, the validation may be skipped.  If false, the validation will execute regardless of errors.  If a validation incurs expensive or time consuming processing it may be useful to set skipOnErrors to true.
     */
    boolean skipOnErrors() default false;

}
