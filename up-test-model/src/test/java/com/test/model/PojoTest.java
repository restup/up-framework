package com.test.model;


import org.junit.Test;
import static org.junit.Assert.*;
import com.github.restup.test.assertions.Assertions;
import com.many.fields.A2J;
import com.model.test.company.Employee;
import com.petstore.Category;
import com.petstore.Order;
import com.petstore.Pet;
import com.petstore.Tag;
import com.petstore.User;

public class PojoTest {

    @Test
    public void testPojos() {
        Assertions.pojo()
        .add(5, "com.deep")
        .add(A2J.class)
        .add(7, "com.model.test.company")
        .add(4, "com.music")
        .add(Category.class, Order.class, Pet.class, Tag.class, User.class)
        .add(3, "com.test.model.animals")
        .add(3, "com.university")
        .validate();
        
    }
    
    @Test
    public void testEmployee() {

        Employee e = new Employee();
        e.setId(1l);
        assertEquals(Long.valueOf(1), e.getId());
    }
    
}
