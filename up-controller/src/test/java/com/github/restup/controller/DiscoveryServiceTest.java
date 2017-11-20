package com.github.restup.controller;

import com.fasterxml.jackson.core.JsonParser;
import org.junit.Test;

public class DiscoveryServiceTest extends AbstractMockTest {

    public DiscoveryServiceTest() {
        super("/", 1);
    }

    @Test
    public void getResources() {
        api.list().ok();
    }

}
