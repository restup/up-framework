package com.github.restup.service.filters;

import com.github.restup.errors.ErrorBuilder;
import com.github.restup.errors.Errors;
import com.github.restup.mapping.fields.IdentityField;
import com.github.restup.registry.Resource;
import com.github.restup.service.model.request.CreateRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SequencedIdValidationFilterTest {

    @Mock
    Errors errors;
    @Mock
    Resource<Object, Long> resource;
    @Mock
    CreateRequest<Object> request;
    @Mock
    IdentityField<Long> idField;

    @Test
    public void testNullId() {
        test(null);
        verify(errors, times(0)).addError(any(ErrorBuilder.class));
    }

    @Test
    public void testNonNullId() {
        test(1l);
        verify(errors, times(1)).addError(any(ErrorBuilder.class));
    }

    private void test(Long id) {
        String foo = "";
        when(resource.getIdentityField()).thenReturn(idField);
        when(request.getData()).thenReturn(foo);
        when(idField.readValue(foo)).thenReturn(id);
        SequencedIdValidationFilter filter = new SequencedIdValidationFilter();
        filter.validateIdNotPresent(errors, resource, request);
    }
}
