package com.github.restup.controller;

import com.github.restup.test.RestApiTest;
import com.university.Course;
import com.university.Student;
import com.university.University;

import org.junit.Before;
import org.junit.Test;

public class CourseServiceJsonAPITest extends AbstractMockTest {

    public CourseServiceJsonAPITest() {
        super(Course.PLURAL_NAME, Course.class
                , Student.class
                , University.class);
        jsonapi();
    }

    @Before
    public void before() {
        super.before();
        loader().relativeTo(CourseServiceTest.class)
                .load("course");
    }

    @Test
    public void createCourse() {
        api.add().ok();
    }

    @Test
    public void error400CreateErrors() {
        api.add().error400();
    }

    @Test
    public void error400PatchErrors() {
        api.patch().error400();
    }

    @Test
    public void getCourse() {
        api.get(2).ok();
    }

    @Test
    public void testRelationships() {
        // examples of fetching relationships between resources
        RestApiTest.Builder api = builder("/courses/{courseId}/university", 5);
        api.get().test("getCourseUniversity").ok();

        // and the reverse works as well
        api = builder("/universities/{universityId}/courses", 1);
        api.get().query("fields=name&limit=1&offset=2&sort=-name").test("getUniversityCourses").ok();
    }

    @Test
    public void listCourses() {
        api.list().query("fields=name").ok();
    }

    @Test
    public void listPaged() {
        api.list().query("fields=name&limit=2&offset=1").ok();
    }

    @Test
    public void listSorted() {
        api.list().query("sort=-universityId,name&fields=name&limit=2").ok();
    }

    @Test
    public void patchCourse() {
        api.patch().ok();
    }

    @Test
    public void testDelete() {
        api.delete().ok();
    }

}
