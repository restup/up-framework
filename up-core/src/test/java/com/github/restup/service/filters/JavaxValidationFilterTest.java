package com.github.restup.service.filters;

import static com.github.restup.util.TestRegistries.mapBackedRegistryBuilder;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import javax.validation.metadata.ConstraintDescriptor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import com.deep.Shallow;
import com.github.restup.errors.Errors;
import com.github.restup.errors.RequestError;
import com.github.restup.path.ResourcePath;
import com.github.restup.registry.Resource;
import com.github.restup.registry.ResourceRegistry;
import com.github.restup.service.model.request.CreateRequest;
import com.github.restup.service.model.request.UpdateRequest;
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
    @Mock
    RequestError.Builder errorBuilder;
    
    @Test
    public void testLength() {
        assertEquals(0, JavaxValidationFilter.length(""));
        assertEquals(0, JavaxValidationFilter.length(" "));
        assertEquals(1, JavaxValidationFilter.length(" 1 "));
        assertEquals(1, JavaxValidationFilter.length("1"));
        assertEquals(0, JavaxValidationFilter.length(Collections.emptyList()));
        assertEquals(1, JavaxValidationFilter.length(Arrays.asList("a")));
        assertEquals(0, JavaxValidationFilter.length(1));
    }
    
    @Test 
    public void testMetaMax() throws InstantiationException, IllegalAccessException {
        testMeta(Max.class, "max");
    }
    
    @Test 
    public void testMetaMin() throws InstantiationException, IllegalAccessException {
        testMeta(Min.class, "min");
    }
    
    @Test 
    public void testMetaSize() throws InstantiationException, IllegalAccessException {
        testMeta(Size.class, "min", "max");
    }
    
    @SuppressWarnings("unchecked")
    public <T extends Annotation> void testMeta(Class<T> ann, String... names) throws InstantiationException, IllegalAccessException {
        T max = mock(ann);
        ConstraintDescriptor<?> constraint = mock(ConstraintDescriptor.class);
        Map<String,Object> map = mock(Map.class);
        
        JavaxValidationFilter filter = new JavaxValidationFilter(validator);
        when(constraint.getAttributes()).thenReturn(map);
        if ( names.length == 1 ) {
            when(map.get("value")).thenReturn(2);
        } else {
            for ( String s : names ) {
                when(map.get(s)).thenReturn(2);
            }
        }

        String value = "foo";
        filter.addMeta(errorBuilder, max, constraint, value);
        verify(errorBuilder).meta("actualLength", value.length());
        for ( String s : names ) {
            verify(errorBuilder).meta(s, 2);
        }
        verify(constraint, times(names.length)).getAttributes();
        if ( names.length == 1 ) {
            verify(map).get("value");
        } else {
            for ( String s : names ) {
                verify(map).get(s);
            }
        }
        verifyNoMoreInteractions(errorBuilder, map, constraint);
    }

    @Test
    public void testCreate() {
        testCreate(validator, Shallow.graph());
        verify(errors, times(0)).addError(any(RequestError.Builder.class));
        verify(validator, times(146)).validateProperty(any(Object.class), any(String.class));
    }

    @Test
    public void testCreateWithErrors() {
        Shallow shallow = Shallow.graph();
        shallow.getDeeps().get(0).getDeepers().get(0).setName(null);
        testCreate(shallow);
        verify(errors, times(1)).addError(any(RequestError.Builder.class));
    }

    @Test
    public void testUpdate() {
        Shallow shallow = Shallow.graph();
        testUpdate(shallow);
        verify(errors, times(0)).addError(any(RequestError.Builder.class));
        verify(validator, times(0)).validateProperty(any(Object.class), any(String.class));

        testUpdate(validator, shallow, "deeps.0.deepers.1.name", "name", "deeps.0.deeper.deepest.depth", "depth");
        verify(errors, times(0)).addError(any(RequestError.Builder.class));
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
        verify(errors, times(1)).addError(any(RequestError.Builder.class));
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
        verify(errors, times(1)).addError(any(RequestError.Builder.class));
    }

    @Test
    public void testUpdatePathWithErrors() {
        Shallow shallow = prepareUpdatePathWithErrors();
        testUpdate(shallow, "deeps.0.deepers");
        verify(errors, times(1)).addError(any(RequestError.Builder.class));
    }

    private <T> void testCreate(T o) {
        testCreate(Validation.buildDefaultValidatorFactory().getValidator(), o);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private <T> void testCreate(Validator validator, T o) {
        JavaxValidationFilter filter = new JavaxValidationFilter(validator);
        ResourceRegistry registry = mapBackedRegistryBuilder()
                .validator(validator).build();
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
        ResourceRegistry registry = mapBackedRegistryBuilder()
                .validator(validator).build();
        registry.registerResource(o.getClass());

        Resource resource = registry.getResource(o.getClass());
        when(update.getData()).thenReturn(o);
        when(update.getRequestedPaths()).thenReturn(ResourcePath.paths(resource, paths));
        filter.validateUpdate(registry, errors, update, resource);
    }

}
