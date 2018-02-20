package com.github.restup.test.spring;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import com.github.restup.test.assertions.Assertions;

@TestExecutionListeners({DependencyInjectionTestExecutionListener.class})
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@WebAppConfiguration
public class MockServiceTest {

    @Autowired
    WebApplicationContext context;
    
    MockMVCTest test;

    @Before
    public void before() {
        test = new MockMVCTest();
        test.mockMvc = 
                MockMvcBuilders
                .webAppContextSetup(context)
                .build();
        test.before();
        test.jsonapi();
        test.before();
    }
    
    @Test
    public void testAdd() {
        Assertions.assertThrows(() -> test.add(), AssertionError.class);
    }
    
    @Test
    public void testList() {
        Assertions.assertThrows(() -> test.list(), AssertionError.class);
    }
    
    @Ignore
    final static class MockMVCTest extends AbstractMockMVCTest {

        public MockMVCTest() {
            super("foo", 1);
        }

        public void add() {
            api.add("foo").requestHeader("bar", "baz").error404();
        }

        public void list() {
            api.list().error404();
        }
    }
}
