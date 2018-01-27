package com.github.restup.bind.converter;

import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import com.github.restup.assertions.Assertions;
import com.github.restup.errors.ErrorFactory;
import com.github.restup.errors.Errors;
import com.github.restup.errors.RequestError;

@RunWith(MockitoJUnitRunner.class)
public class FunctionalParameterConverterTest {
    
    @Mock
    private Errors errors;
    
    @Test
    public void testConversionException() {
        FunctionalParameterConverter<?> converter = new FunctionalParameterConverter<>(a->{throw new IllegalArgumentException();}, ErrorFactory.getDefaultErrorFactory());
        
        String parameterName = "foo";
        String from = "f";
        assertNull(converter.convert(parameterName, from , errors));

        ArgumentCaptor<RequestError.Builder> b = ArgumentCaptor.forClass(RequestError.Builder.class);
        verify(errors).addError(b.capture());
        
        Assertions.assertThat(b.getValue())
            .code("PARAMETER_CONVERSION")
            .title("Conversion Error")
            .detail("Unable to convert value to correct type")
            .meta(parameterName, from);

        verifyNoMoreInteractions(errors);
    }

}
