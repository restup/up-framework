package com.github.restup.util;

import com.github.restup.mapping.fields.visitors.IdentityByConventionMappedFieldBuilderVisitor;
import com.github.restup.registry.ResourceRegistry;
import com.github.restup.repository.collections.MapBackedRepositoryFactory;
import com.university.Course;
import com.university.University;

public class TestRegistries {

    public static ResourceRegistry defaultRegistry() {
        return ResourceRegistry.builder().build();
    }

    public static ResourceRegistry.Builder mapBackedRegistryBuilder() {
        return ResourceRegistry.builder()
                        .repositoryFactory(new MapBackedRepositoryFactory())
                        .mappedFieldBuilderVisitors(new IdentityByConventionMappedFieldBuilderVisitor()
        );
    }

    public static ResourceRegistry mapBackedRegistry() {
        return mapBackedRegistryBuilder().build();
    }
    
    public static ResourceRegistry universityRegistry() {
        ResourceRegistry registry = mapBackedRegistry();
        registry.registerResources(University.class, Course.class);
        return registry;
    }
    
    private TestRegistries() {
        
    }
}
