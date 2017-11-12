package com.github.restup.query.criteria;

import org.junit.Assert;
import org.junit.Test;

public class OperatorTest {

    @Test
    public void testOf() {
        Assert.assertEquals(ResourcePathFilter.Operator.eq, ResourcePathFilter.Operator.of(null));
        for (ResourcePathFilter.Operator op : ResourcePathFilter.Operator.values()) {
            Assert.assertEquals(op, ResourcePathFilter.Operator.of(op.name()));
            for (String s : op.getOperators()) {
                Assert.assertEquals(op, ResourcePathFilter.Operator.of(s));
            }
        }
    }

}
