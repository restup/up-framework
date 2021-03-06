package com.github.restup.errors;

import static com.github.restup.assertions.Assertions.assertThat;
import static com.github.restup.assertions.Assertions.assertThrows;
import static com.github.restup.errors.RequestError.builder;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import com.github.restup.path.ResourcePath;
import com.github.restup.registry.Resource;
import com.github.restup.registry.ResourceRegistry;
import com.github.restup.test.assertions.Assertions;
import org.junit.Test;
import org.mockito.Mockito;

public class RequestErrorTest {

    @Test
    public void testError() {
        String pattern = "This is a {0}";
        Object[] patternArgs = {"test"};
        BasicRequestError error = (BasicRequestError)builder().id("foo")
        .meta("bar")
        .httpStatus(418)
        .code(ErrorCode.UNKNOWN_RESOURCE)
        .detail(pattern, patternArgs)
        .build();
        
        assertThat(error)
        .meta("bar")
        .id("foo")
        .httpStatus(418)
        .detail("This is a test")
        .status("418");

        assertEquals(pattern, error.getDetailPattern());
        assertEquals(patternArgs[0], error.getDetailPatternArgs()[0]);
    }

    @Test
    public void testMeta() {
        assertThat(builder()
                .meta("foo", "a")
                .meta("bar", "b")
            .build())
        .meta("foo","a")
        .meta("bar","b");
    }

    @Test
    public void testErrorCodeDetail() {
        assertThat(builder()
                .code(ErrorCode.BODY_INVALID)
            .build())
        .detail(ErrorCode.BODY_INVALID.getDetail());
    }

    @Test
    public void testMetaError() {
        Assertions.assertThrows(()->builder().id("foo")
            .meta("bar")
            .meta("a","b"), IllegalArgumentException.class)
            .hasMessage("Unable to set Meta with key value pairs")
            .hasNoCause();
    }

    @Test
    public void testBuilderThrowError() {
        assertThrows(()->builder().throwError())
            .hasNoCause();
    }
    
    @Test
    public void testResourcePath() {
        ResourcePath path = Mockito.mock(ResourcePath.class);
        Resource resource = Mockito.mock(Resource.class);
        when(path.getResource()).thenReturn(resource);
        ResourceRegistry registry = Mockito.mock(ResourceRegistry.class);
        when(resource.getRegistry()).thenReturn(registry);
        assertThat( builder( path ) 
                );
    }

    @Test
    public void testLogException() {
        RequestErrorException ex = RequestErrorException.of(new IllegalArgumentException());
        BasicRequestError err = (BasicRequestError) ex.getPrimaryError();
        assertEquals(err.getCause(), err.logStackTrace());
    }
    
    @Test
    public void testLogErrorDebugDisabled() {
        String title = "foo";
        BasicRequestError err = new BasicRequestError(null, null, title, null, null, null, null, null, 0, null, null);
        assertEquals(err.getTitle(), err.logStackTrace());
    }
    
    
}
