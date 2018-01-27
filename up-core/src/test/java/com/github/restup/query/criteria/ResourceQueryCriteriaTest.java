package com.github.restup.query.criteria;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import org.junit.Test;

public class ResourceQueryCriteriaTest {
    
    private ResourceQueryCriteria criteria(Object target, boolean result) {
        ResourceQueryCriteria criteria = mock(ResourceQueryCriteria.class);
        when(criteria.filter(target)).thenReturn(result);
        return criteria;
    }

    @Test
    public void testAndFalse() {
        Object foo = "";
        ResourceQueryCriteria positive = criteria(foo, true);
        ResourceQueryCriteria negative = criteria(foo, false);
        
        assertFalse(ResourceQueryCriteria.and(positive, negative).filter(foo));

        // negative filter applied positive will not be
        ResourceQueryCriteria negative2 = criteria(foo, false);
        assertFalse(ResourceQueryCriteria.and(negative2, positive).filter(foo));

        verifyFilter(foo, positive, negative, negative2);
        verifyNoMoreInteractions(positive, negative, negative2);
    }

    @Test
    public void testAndTrue() {
        Object foo = "";
        ResourceQueryCriteria a = criteria(foo, true);
        ResourceQueryCriteria b = criteria(foo, true);
        
        assertTrue(ResourceQueryCriteria.and(a, b).filter(foo));

        verifyFilter(foo, a, b);
        verifyNoMoreInteractions(a, b);
    }

    @Test
    public void testOrFalse() {
        Object foo = "";
        ResourceQueryCriteria a = criteria(foo, false);
        ResourceQueryCriteria b = criteria(foo, false);
        
        assertFalse(ResourceQueryCriteria.or(a, b).filter(foo));

        verifyFilter(foo, a, b);
        verifyNoMoreInteractions(a, b);
    }

    @Test
    public void testOrTrue() {
        Object foo = "";
        ResourceQueryCriteria a = criteria(foo, true);
        ResourceQueryCriteria b = criteria(foo, true);
        
        // b verify not executed
        assertTrue(ResourceQueryCriteria.or(a, b).filter(foo));
        
        ResourceQueryCriteria c = criteria(foo, false);
        assertTrue(ResourceQueryCriteria.or(c, b).filter(foo));

        verifyFilter(foo, a, b, c);
        verifyNoMoreInteractions(a, b);
    }

    private void verifyFilter(Object target, ResourceQueryCriteria... array) {
        for ( ResourceQueryCriteria criteria : array) {
            verify(criteria).filter(target);
        }
    }
    
}
