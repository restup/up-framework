package com.github.restup.repository;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.junit.Test;
import com.github.restup.registry.Resource;
import com.github.restup.registry.ResourceRegistry;
import com.github.restup.registry.settings.RegistrySettings;
import com.github.restup.registry.settings.ServiceMethodAccess;

public class AnnotatedResourceRepositoryTest {

    @Test
    public void testGetRepository() {
        ResourceRepository<?,?> repo = mock(ResourceRepository.class);
        Resource<?,?> resource = mock(Resource.class);
        when(resource.getServiceAccess()).thenReturn(mock(ServiceMethodAccess.class));
        ResourceRegistry registry = mock(ResourceRegistry.class);
        when(resource.getRegistry()).thenReturn(registry);
        when(registry.getSettings()).thenReturn(mock(RegistrySettings.class));
        AnnotatedResourceRepository annotatedResourceRepository = new AnnotatedResourceRepository(resource, repo);
        assertEquals(repo, annotatedResourceRepository.getRepository());
    }
}
