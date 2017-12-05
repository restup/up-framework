package com.github.restup.annotations.field;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Annotation which defines a relationship between resources
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RUNTIME)
@Documented
public @interface Relationship {

    /**
     * The name of the relationship.  If left as the default value, the resource name will be used
     */
    String name() default "";

    /**
     * The resource class has a relationship to
     */
    Class<?> resource();

    /**
     * The field of {@link #resource()} that the annotated field has a relationship to
     */
    String joinField() default "id";

    /**
     * If true, create and updates will validate references
     */
    boolean validateReferences() default true;

    /**
     * If true, the resource may be included
     */
    boolean includable() default true;

    /**
     * By default, relationships are assumed to be "to many", setting to true will treat the relationships as 1:1
     */
    RelationshipType type() default RelationshipType.manyToOne;
}
