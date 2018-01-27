package com.github.restup.errors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static com.github.restup.assertions.Assertions.assertThat;
import static com.github.restup.errors.RequestError.buildException;
import static com.github.restup.errors.RequestError.builder;
import static com.github.restup.errors.RequestError.throwError;
import org.junit.Test;
import static org.junit.Assert.*;
import org.mockito.Mockito;
import static org.mockito.Mockito.*;
import static com.github.restup.assertions.Assertions.*;
import com.github.restup.path.ResourcePath;
import com.github.restup.registry.Resource;
import com.github.restup.registry.ResourceRegistry;
import com.github.restup.test.assertions.Assertions;

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
    public void testThrowError() {
        assertThrows(()->throwError(new IllegalArgumentException()))
        .hasCauseExactlyInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void testBuilderThrowError() {
        assertThrows(()->builder().throwError())
            .hasNoCause();
    }
    
    @SuppressWarnings({"rawtypes", "unchecked"})
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
        ErrorObjectException ex = buildException(new IllegalArgumentException());
        BasicRequestError err = (BasicRequestError) ex.getPrimaryError();
        assertEquals(err.getCause(), err.logStackTrace());
    }

    @Test
    public void testLogError() {
        BasicRequestError err = (BasicRequestError) builder().title("foo").build();
        assertThat(err.logStackTrace(), instanceOf(BasicRequestError.StackDetail.class));
        assertNotNull(err.getStackTrace());
    }
    
    @Test
    public void testLogErrorDebugDisabled() {
        String title = "foo";
        BasicRequestError err = new BasicRequestError(null, null, title, null, null, null, null, null, 0, null, null);
        assertEquals(err.getTitle(), err.logStackTrace());
    }
    
    
}
