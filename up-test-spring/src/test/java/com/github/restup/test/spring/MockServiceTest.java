package com.github.restup.test.spring;

import org.junit.Before;
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
        swallow(() -> test.add() );
    }
    
    @Test
    public void testList() {
        swallow(() -> test.list());
    }
    
    private void swallow(Runnable r) {
        // we don't care about the mock test assertions
        try {
            r.run();
        } catch (Error e) {
            
        }
    }
    
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
