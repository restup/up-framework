package com.github.restup.spring.boot;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.github.restup.registry.ResourceRegistry;
import com.test.model.animals.Zoo;
import com.university.Course;
import com.university.Student;
import com.university.University;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringBootStarterTests {

    @Autowired
    private ResourceRegistry registry;

    @Test
    public void contextLoads() {
        assertNotNull(registry.getResource(Course.class));
        assertNotNull(registry.getResource(Student.class));
        assertNotNull(registry.getResource(University.class));
        assertNull(registry.getResource(Zoo.class));
    }

}
