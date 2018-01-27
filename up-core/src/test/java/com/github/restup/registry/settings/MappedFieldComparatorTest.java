package com.github.restup.registry.settings;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.junit.Test;
import com.github.restup.mapping.fields.MappedField;
import com.github.restup.registry.settings.RegistrySettings.MappedFieldComparator;

public class MappedFieldComparatorTest {

    MappedFieldComparator comparator = new MappedFieldComparator();

    @Test
    public void testCompareTo() {
        assertTrue(comparator.compare(null, null) == 0);
        assertTrue(comparator.compare(apiName("a"), null) == -1);
        assertTrue(comparator.compare(null, apiName("a")) == 1);

        assertTrue(comparator.compare(identifier(true), identifier(true)) == 0);
        assertTrue(comparator.compare(identifier(true), identifier(false)) == -1);
        assertTrue(comparator.compare(identifier(false), identifier(true)) == 1);

        assertTrue(comparator.compare(apiName("a"), apiName("a")) == 0);
        assertTrue(comparator.compare(apiName("a"), apiName("b")) == -1);
        assertTrue(comparator.compare(apiName("c"), apiName("b")) == 1);

        assertTrue(comparator.compare(beanName("a"), beanName("a")) == 0);
        assertTrue(comparator.compare(beanName("a"), beanName("b")) == -1);
        assertTrue(comparator.compare(beanName("c"), beanName("b")) == 1);
    }

    private MappedField<?> apiName(String name) {
        MappedField<?> a = mock(MappedField.class);
        when(a.getApiName()).thenReturn(name);
        return a;
    }

    private MappedField<?> beanName(String name) {
        MappedField<?> a = mock(MappedField.class);
        when(a.getBeanName()).thenReturn(name);
        return a;
    }

    private MappedField<?> identifier(boolean b) {
        MappedField<?> a = mock(MappedField.class);
        when(a.isIdentifier()).thenReturn(b);
        return a;
    }

}
