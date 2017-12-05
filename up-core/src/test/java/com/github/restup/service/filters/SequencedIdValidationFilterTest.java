package com.github.restup.service.filters;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.restup.errors.ErrorBuilder;
import com.github.restup.errors.Errors;
import com.github.restup.mapping.fields.MappedField;
import com.github.restup.registry.Resource;
import com.github.restup.service.model.request.CreateRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class SequencedIdValidationFilterTest {

    @Mock
    Errors errors;
    @Mock
    Resource<Object, Long> resource;
    @Mock
    CreateRequest<Object> request;
    @Mock
    MappedField<Long> idField;

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
