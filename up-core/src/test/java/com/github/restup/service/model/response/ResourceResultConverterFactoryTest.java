package com.github.restup.service.model.response;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.mockito.Mockito.mock;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.junit.Test;
import com.github.restup.annotations.operations.BulkCreateResource;
import com.github.restup.annotations.operations.BulkDeleteResource;
import com.github.restup.annotations.operations.BulkUpdateResource;
import com.github.restup.annotations.operations.CreateResource;
import com.github.restup.annotations.operations.DeleteResource;
import com.github.restup.annotations.operations.DeleteResourceByQuery;
import com.github.restup.annotations.operations.ListResource;
import com.github.restup.annotations.operations.ReadResource;
import com.github.restup.annotations.operations.UpdateResource;
import com.github.restup.annotations.operations.UpdateResourceByQuery;
import com.model.test.company.Person;

public class ResourceResultConverterFactoryTest {

    private ResourceResultConverterFactory factory = ResourceResultConverterFactory.getInstance();

    @Test
    public void testBulkResultConverter() {
        List<Class<? extends Annotation>> list = Arrays.asList(BulkCreateResource.class,
                BulkUpdateResource.class, 
                BulkDeleteResource.class,
                UpdateResourceByQuery.class,
                DeleteResourceByQuery.class);
        assertConvert(list
        , List.class, PersistenceResult.class);
        assertConvert(list
        , Collections.emptySet(), PersistenceResult.class);
        assertConvert(list
        , Person.class, PersistenceResult.class);
    }

    @Test
    public void testResultConverter() {
        assertConvert(Arrays.asList(CreateResource.class,
                UpdateResource.class, 
                DeleteResource.class)
        , Person.class, PersistenceResult.class);
    }

    @Test
    public void testReadResultConverter() {
        assertConvert(Arrays.asList(ListResource.class)
        , Person.class, ReadResult.class);
    }

    @Test
    public void testListResultConverter() {
        assertConvert(Arrays.asList(ReadResource.class)
        , Collection.class, ReadResult.class);
    }

    @Test
    public void testNoOpConverter() {
        assertConvert(Arrays.asList(Test.class)
        , Collection.class, Collection.class);
    }

    private void assertConvert(List<Class<? extends Annotation>> annotations, Class<?> targetToMock, Class<?> expectInstanceOf) {
        assertConvert(annotations, mock(targetToMock), expectInstanceOf);
    }
    
    private void assertConvert(List<Class<? extends Annotation>> annotations, Object mockedTarget, Class<?> expectInstanceOf) {
        annotations.stream().forEach(a -> {
            ResourceResultConverter converter = factory.getConverter(a);
            assertThat(converter.convert(mockedTarget), instanceOf(expectInstanceOf) );
            assertThat(converter.convert(mock(expectInstanceOf)), instanceOf(expectInstanceOf) );           
        });
    }
    
}
