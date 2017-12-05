package com.github.restup.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to define Resource annotation parsing behavior. Using {@link Strict} will require that annotations are present to define persistence or json names so that default bean names will not be used. <p>For example, if {@link #json()} is true and using Jackson only fields annotated using @JsonProperty will be used in ResourceDescriptions
 *
 * @author andy.buttaro
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Strict {

    boolean persistence() default true;

    boolean json() default true;

}
