package com.github.restup.service.model.response;

import org.junit.Test;

import static org.junit.Assert.*;

public class BasicPagedResultTest {

    @Test
    public void testLimitAndOffset() {
        PagedResult result = PagedResult.of(null, null, 0l);
        assertNull(result.getLimit());
        assertNull(result.getOffset());
    }

}
