package com.github.restup.service.filters;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.deep.Shallow;
import com.github.restup.errors.ErrorBuilder;
import com.github.restup.errors.Errors;
import com.github.restup.path.ResourcePath;
import com.github.restup.registry.Resource;
import com.github.restup.registry.ResourceRegistry;
import com.github.restup.registry.settings.RegistrySettings;
import com.github.restup.repository.collections.MapBackedRepositoryFactory;
import com.github.restup.service.model.request.CreateRequest;
import com.github.restup.service.model.request.UpdateRequest;
import javax.validation.Validation;
import javax.validation.Validator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class JavaxValidationFilterTest {

    @Mock
    CreateRequest<Object> create;
    @Mock
    UpdateRequest<Object, ?> update;
    @Mock
    Errors errors;
    @Mock
    Validator validator;

    @Test
    public void testCreate() {
        testCreate(validator, Shallow.graph());
        verify(errors, times(0)).addError(any(ErrorBuilder.class));
        verify(validator, times(146)).validateProperty(any(Object.class), any(String.class));
    }

    @Test
    public void testCreateWithErrors() {
        Shallow shallow = Shallow.graph();
        shallow.getDeeps().get(0).getDeepers().get(0).setName(null);
        testCreate(shallow);
        verify(errors, times(1)).addError(any(ErrorBuilder.class));
    }

    @Test
    public void testUpdate() {
        Shallow shallow = Shallow.graph();
        testUpdate(shallow);
        verify(errors, times(0)).addError(any(ErrorBuilder.class));
        verify(validator, times(0)).validateProperty(any(Object.class), any(String.class));

        testUpdate(validator, shallow, "deeps.0.deepers.1.name", "name", "deeps.0.deeper.deepest.depth", "depth");
        verify(errors, times(0)).addError(any(ErrorBuilder.class));
        verify(validator, times(1)).validateProperty(shallow.getDeeps().get(0).getDeepers().get(1), "name");
        verify(validator, times(1)).validateProperty(shallow, "name");
        verify(validator, times(1)).validateProperty(shallow.getDeeps().get(0).getDeeper().getDeepest(), "depth");
        verify(validator, times(1)).validateProperty(shallow, "depth");
        verify(validator, times(4)).validateProperty(any(Object.class), any(String.class));
    }

    @Test
    public void testUpdateWithErrors() {
        Shallow shallow = Shallow.graph();
        shallow.getDeeps().get(0).getDeepers().get(0).setName(null);
        shallow.getDeeps().get(1).getDeepers().get(1).setName(null);
        shallow.setName(null);
        testUpdate(shallow);
        testUpdate(shallow, "deeps.0.deepers.0.name", "deeps.0.deeper.deepest.depth", "depth");
        verify(errors, times(1)).addError(any(ErrorBuilder.class));
    }

    private Shallow prepareUpdatePathWithErrors() {
        Shallow shallow = Shallow.graph();
        shallow.getDeeps().get(0).getDeepers().get(0).setName(null);
        shallow.getDeeps().get(1).getDeepers().get(1).setName(null);
        shallow.setName(null);
        return shallow;
    }

    @Test
    public void testUpdateArrayPathWithErrors() {
        Shallow shallow = prepareUpdatePathWithErrors();
        testUpdate(shallow, "deeps.0");
        verify(errors, times(1)).addError(any(ErrorBuilder.class));
    }

    @Test
    public void testUpdatePathWithErrors() {
        Shallow shallow = prepareUpdatePathWithErrors();
        testUpdate(shallow, "deeps.0.deepers");
        verify(errors, times(1)).addError(any(ErrorBuilder.class));
    }

    private <T> void testCreate(T o) {
        testCreate(Validation.buildDefaultValidatorFactory().getValidator(), o);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private <T> void testCreate(Validator validator, T o) {
        JavaxValidationFilter filter = new JavaxValidationFilter(validator);
        ResourceRegistry registry = new ResourceRegistry(RegistrySettings.builder()
                .repositoryFactory(new MapBackedRepositoryFactory())
                .validator(validator));
        registry.registerResource(o.getClass());

        when(create.getData()).thenReturn(o);
        filter.validateCreate(registry, errors, create, (Resource) registry.getResource(o.getClass()));
    }

    private <T> void testUpdate(T o, String... paths) {
        testUpdate(Validation.buildDefaultValidatorFactory().getValidator(), o, paths);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private <T> void testUpdate(Validator validator, T o, String... paths) {
        JavaxValidationFilter filter = new JavaxValidationFilter(validator);
        ResourceRegistry registry = new ResourceRegistry(RegistrySettings.builder()
                .repositoryFactory(new MapBackedRepositoryFactory())
                .validator(validator));
        registry.registerResource(o.getClass());

        Resource resource = (Resource) registry.getResource(o.getClass());
        when(update.getData()).thenReturn(o);
        when(update.getRequestedPaths()).thenReturn(ResourcePath.paths(resource, paths));
        filter.validateUpdate(registry, errors, update, resource);
    }

}
