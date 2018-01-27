package com.github.restup.query.criteria;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import org.junit.Test;
import com.github.restup.query.criteria.ResourcePathFilter.Operator;

public class OperatorTest {

    @Test
    public void testOf() {
        assertEquals(ResourcePathFilter.Operator.eq, ResourcePathFilter.Operator.of(null));
        for (ResourcePathFilter.Operator op : ResourcePathFilter.Operator.values()) {
            assertEquals(op, ResourcePathFilter.Operator.of(op.name()));
            for (String s : op.getOperators()) {
                assertEquals(op, ResourcePathFilter.Operator.of(s));
            }
        }
        assertNull(Operator.of("bad"));
    }

}
