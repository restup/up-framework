package com.github.restup.service.filters;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.io.Serializable;
import java.util.UUID;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import com.github.restup.assertions.Assertions;
import com.github.restup.registry.Resource;
import com.github.restup.service.model.request.ReadRequest;
import com.github.restup.service.model.response.ReadResult;
import com.model.test.company.Person;

@SuppressWarnings({"unchecked", "rawtypes"})
@RunWith(MockitoJUnitRunner.class)
public class NotFoundFilterTest {
    
    @Mock
    private Resource resource;
    @Mock
    private ReadRequest request;
    private final String ID = UUID.randomUUID().toString();
    
    private NotFoundFilter filter = new NotFoundFilter();
    
    @Before
    public void before() {
        when(request.getId()).thenReturn(ID);
    }
    
    @Test
    public <T, ID extends Serializable> void testNotFoundNullResult() {
        assertResourceNotFound(resource, request, null, ID);
    }
    
    @Test
    public <T, ID extends Serializable> void testNotFoundNullResultData() {
        ReadResult<T> result = mock(ReadResult.class);
        assertResourceNotFound(resource, request, result, ID);
    }
    
    @Test
    public void testResultFound() {
        ReadResult<Object> result = mock(ReadResult.class);
        when(result.getData()).thenReturn(new Person());
        // no exception thrown
        filter.assertResourceNotFound(resource, request, result);
    }

    private <T, ID extends Serializable> void assertResourceNotFound(Resource<T, ID> resource, ReadRequest<T, ID> request, ReadResult<T> result, ID id) {
        Assertions.assertThrows(
                () -> filter.assertResourceNotFound(resource, request, result)
                ).code("RESOURCE_NOT_FOUND")
        .detail("Resource not found")
        .meta("id", id);
    }

}
