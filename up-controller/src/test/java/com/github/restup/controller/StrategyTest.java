package com.github.restup.controller;

import static org.junit.Assert.assertEquals;

import com.data.DataPoint;
import com.data.Message;
import com.github.restup.controller.mock.ApiTest;
import com.github.restup.test.RestApiAssertions;
import java.util.UUID;
import org.junit.Test;

public class StrategyTest {

    @Test
    public void testAccepted() {
        String id = UUID.randomUUID().toString();
        RestApiAssertions.Builder api = ApiTest.builder(DataPoint.class)
            .decorateApi((b) -> b.contentsAssertions(false))
            .build()
            .getApi("/data", id);

        api.add("{\"data\":{\"id\":\"" + id + "\",\"label\":\"foo\",\"value\":\"1\"}}").accepted();
        assertEquals("foo", api.get().ok().read("data.label"));
        api.update("{\"data\":{\"label\":\"bar\"}}").accepted();
        assertEquals("bar", api.get().ok().read("data.label"));
        api.delete().accepted();
        api.get().notFound();
    }

    @Test
    public void testNoContent() {
        String id = UUID.randomUUID().toString();
        RestApiAssertions.Builder api = ApiTest.builder()
            .registerResources(Message.class)
            .decorateApi((b) -> b.contentsAssertions(false))
            .build()
            .getApi("/messages", id);

        api.add("{\"data\":{\"id\":\"" + id + "\",\"message\":\"foo\"}}").noContent();
        assertEquals("foo", api.get().ok().read("data.message"));
        api.update("{\"data\":{\"message\":\"bar\"}}").noContent();
        assertEquals("bar", api.get().ok().read("data.message"));
        api.delete().noContent();
        api.get().notFound();
    }

}
