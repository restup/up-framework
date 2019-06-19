package com.github.restup.aws.lambda;

import com.github.restup.aws.lambda.support.AbstractAWSLambdaTest;
import com.github.restup.test.RestApiAssertions;
import com.mutable.university.Course;
import com.mutable.university.Student;
import com.mutable.university.University;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class CourseServiceTest extends AbstractAWSLambdaTest {

    public CourseServiceTest() {
        super("/courses", Course.class
            , Student.class
            , University.class);
    }

    @Before
    public void setup() {
        super.before();
        loader().load("course");
    }

    @Test
    public void getCourse() {
        api.get(2).ok();
    }

    @Test
    public void listCourses() {
        api.list().query("fields=name").ignoreOrder().ok();
    }

    @Test
    public void listCoursesIncludeUniversity() {
        api.list().query("fields=name&fields[university]=name").ignoreOrder().ok();
    }

    @Test
    public void listPaged() {
        api.list().query("fields=name&limit=2&filter[universityId]=1&sort=-name").ok();
    }

    @Test
    public void testRelationships() {
        // examples of fetching relationships between resources
        RestApiAssertions.Builder api = builder("/courses/{courseId}/university", 5);
        api.get().test("getCourseUniversity").ok();

        // and the reverse works as well
        api = builder("/universities/{universityId}/courses", 1);
        api.get().query("fields=name&limit=1&sort=name").test("getUniversityCourses")
            .ok();
    }

    @Test
    public void createCourse() {
        api.add().created();
    }

    @Test
    public void patchCourse() {
        api.patch().ok();
    }

    @Test
    public void testDelete() {
        api.delete().noContent();
    }

    @Test
    @Ignore
    public void testFilters() {
//        count("filter[name][like]=_e*", 1);
//        count("filter[name][exists]=1", 12);
//        count("filter[name][exists]=NO", 0);
//        count("filter[name][like]=_B*", 1);
//        count("filter[name][like]=*d*", 1);
        count("filter[name]=Spanish 101", 1);
        count("filter[name][eq]=SPANISH 101", 1);
        count("filter[name][ne]=Spanish 101", 5);
        count("filter[name]=physics&filter[name]=algebra", 2);
        count("filter[name][eq]=Calculus&filter[name][eq]=Computer Science", 2);
        count("filter[name][in]=Calculus&filter[name][in]=Computer Science", 2);
        count("filter[name][ne]=Calculus&filter[name][ne]=Algebra", 4);
        count("filter[name][nin]=French 101&filter[name][nin]=Spanish 101", 4);
        count("filter[name][gte]=P", 2);
        count("filter[name][gt]=Physics", 1);
        count("filter[name][gte]=C&filter[universityId]=2&filter[name][lt]=D", 2);
        count("filter[name][lt]=C", 1);
        count("filter[name][lte]=Calculus", 2);
        count("filter[name][lte]=s&filter[name][gt]=e", 2);
    }

    private void count(String query, int n) {
        api.list().query(query + "&limit=0").expectBody("{\"total\":" + n + "}").ok();
    }
}
