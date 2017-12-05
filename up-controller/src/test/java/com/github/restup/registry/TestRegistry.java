package com.github.restup.registry;

import com.github.restup.registry.settings.RegistrySettings;
import com.github.restup.repository.collections.MapBackedRepositoryFactory;

public class TestRegistry {

    public static ResourceRegistry registry(String... packagesToScan) {
        return new ResourceRegistry(RegistrySettings.builder()
                .repositoryFactory(new MapBackedRepositoryFactory())
                .packagesToScan(packagesToScan));
    }

}
