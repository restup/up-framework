package com.github.restup.annotations;

import com.github.restup.annotations.model.CreateStrategy;
import com.github.restup.annotations.model.DeleteStrategy;
import com.github.restup.annotations.model.UpdateStrategy;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Optional tag annotation for resources, which may aid component scanning.
 * 
 * @author abuttaro
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Resource {

    /**
     * Enables or disables index requirement in queries.  Use with caution as allowing access withut
     * using indexes will not perform well in most situations. If false, permits full table scans.
     * Ultimately it is up to the Repository implementation to enforce this behavior.
     */
    boolean indexedQueryOnly() default true;

    CreateStrategy createStrategy() default CreateStrategy.DEFAULT;

    UpdateStrategy updateStrategy() default UpdateStrategy.DEFAULT;

    DeleteStrategy deleteStrategy() default DeleteStrategy.DEFAULT;

}
