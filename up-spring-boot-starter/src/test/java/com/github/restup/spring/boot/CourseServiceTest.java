package com.github.restup.spring.boot;

import static com.github.restup.test.resource.RelativeTestResource.request;
import static com.github.restup.test.resource.RelativeTestResource.response;
import static com.github.springtestdbunit.assertion.DatabaseAssertionMode.NON_STRICT;
import static net.javacrumbs.jsonunit.JsonMatchers.jsonPartEquals;

import com.github.restup.test.RestApiAssertions;
import com.github.restup.test.RpcApiAssertions.Builder;
import com.github.restup.test.spring.AbstractMockMVCTest;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import org.dbunit.dataset.Column;
import org.dbunit.dataset.filter.IColumnFilter;
import org.junit.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

@SpringBootTest
@AutoConfigureMockMvc
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class,
    DbUnitTestExecutionListener.class})
@TestPropertySource(properties = {
    "up.controller.async=false",
    "up.dynamodb.enabled=false"
})
public class CourseServiceTest extends AbstractMockMVCTest {

    protected static final String BASE = "/com/github/restup/spring/boot/CourseServiceTest/";
    protected static final String RESULTS = BASE + "results/";
    protected static final String SETUP = BASE + "dumps/";
    protected static final String COURSE_XML = SETUP + "course.xml";
    protected static final String FILTERS_XML = SETUP + "filtering.xml";

    public CourseServiceTest() {
        super("/courses", 1);
    }

    @Test
    @DatabaseSetup(COURSE_XML)
    public void testListEmpty() {
        // different ways of doing the same thing
        // 1. Specify body as String
        listEmpty().expectBody("{\"data\":[],\"total\":0,\"offset\":0,\"limit\":10}").ok();

        // 2. Specify body as a relative response resource
        listEmpty().expectBody(response("listEmpty")).ok();

        // 3. Use named test, which adds relative response resource by default
        listEmpty().test("listEmpty").ok();
    }

    private Builder listEmpty() {
        return api.list().param("filter[universityId]", "61616161");
    }

    @Test
    @DatabaseSetup(COURSE_XML)
    public void testGet() {
        // different ways of doing the same thing
        // 1. Specify expected body as String
        api.get().expectBody(
            "{\"data\":{\"id\":1,\"type\":\"course\",\"name\":\"Physics\",\"universityId\":1}}")
            .ok();

        // 2. Specify body as a relative response resource
        api.get().expectBody(response("getCourse")).ok();

        // 3. Use named test, which adds relative response resource by default
        api.get().test("getCourse").ok();

        // 4. pass id explicitly (default from constructor use in previous examples)
        api.get(1).test("getCourse").ok();
    }

    @Test
    @DatabaseSetup(COURSE_XML)
    public void testList() {
        api.list().test("listCourses").ok();
    }

    @Test
    @DatabaseSetup(COURSE_XML)
    @ExpectedDatabase(value = RESULTS
        + "createCourse.xml", table = "Course", assertionMode = NON_STRICT, columnFilters = IDFilter.class)
    public void testCreate() {
        // 1. Specify body as string
        api.add("{\"data\":{\"name\":\"Bar\",\"universityId\": 1}}")
            .expectBody(
                "{\"data\":{\"id\":\"${json-unit.ignore}\",\"type\":\"course\",\"name\":\"Bar\",\"universityId\":1}}")
            .created();

        // 2. Specify body as a relative request resource and expectedBody as a relative response resource
        api.add(request("createCourse")).expectBody(response("createCourse")).created();

        // 3. Use named test, which adds relative response and resource by default
        api.add().test("createCourse").created();
    }

    @Test
    @DatabaseSetup(COURSE_XML)
    @ExpectedDatabase(value = RESULTS + "patchCourse.xml", assertionMode = NON_STRICT)
    public void testPatch() {

        // 2. Specify body as a relative request resource and expectedBody as a relative response resource
        api.patch(request("patchCourse")).expectBody(response("patchCourse")).ok();

        // 3. Use named test, which adds relative response and resource by default
        api.patch().test("patchCourse").ok();
    }

    @Test
    @DatabaseSetup(COURSE_XML)
    @ExpectedDatabase(value = RESULTS + "deleteCourse.xml", assertionMode = NON_STRICT)
    public void testDelete() {
        // 1. Specify expected body as String
        api.delete().expectBody(
            "{\"data\":{\"id\":1,\"type\":\"course\",\"name\":\"Physics\",\"universityId\":1}}")
            .noContent();

        // 2. Specify body as a relative response resource
        api.delete(2).expectBody(response("deleteCourse2")).noContent();

        // 3. Use named test, which adds relative response resource by default
        api.delete(3).test("deleteCourse3").noContent();
    }

    @Test
    @DatabaseSetup(COURSE_XML)
    public void testRelationships() {
        // examples of fetching relationships between resources
        RestApiAssertions.Builder api = builder("/courses/{courseId}/university", 5);
        api.get().test("getCourseUniversity").ok();

        // and the reverse works as well
        api = builder("/universities/{universityId}/courses", 1);
        api.get().query("fields=name&limit=1&offset=2&sort=-name").test("getUniversityCourses")
            .ok();
    }

    @Test
    public void testError400InvalidUniversityId() {
        api.add().test("error400InvalidUniversityId").error400();
    }

    @Test
    public void testError404NotFound() {
        api.get(616161616).test("error404NotFound").error404();

        api.get(1234565432)
            .test("error404NotFound")
            .bodyMatcher(jsonPartEquals("errors[0].code", "RESOURCE_NOT_FOUND"))
            .error404();

    }

    @Test
    @DatabaseSetup(FILTERS_XML)
    public void testFilters() {
        count("filter[name][like]=_e*", 1);
        count("filter[name][exists]=1", 12);
        count("filter[name][exists]=NO", 0);
        count("filter[name][like]=_B*", 1);
        count("filter[name][like]=*d*", 1);
        count("filter[name]=Bbb", 1);
        count("filter[name][eq]=Ccc", 1);
        count("filter[name][ne]=foo", 12);
        count("filter[name]=ccc&filter[name]=DdD", 2);
        count("filter[name][eq]=Aaa&filter[name][eq]=Bbb", 2);
        count("filter[name][in]=Aaa&filter[name][in]=cCC", 2);
        count("filter[name][ne]=Aaa&filter[name][ne]=Bbb", 10);
        count("filter[name][nin]=Ccc&filter[name][nin]=Ddd", 10);
        count("filter[name][gte]=Y", 2);
        count("filter[name][gt]=yyy", 1);
        count("filter[name][lt]=bbb", 1);
        count("filter[name][lte]=ccb", 2);
        count("filter[name][lte]=cCc&filter[name][gt]=aAa", 2);
    }

    private void count(String query, int n) {
        api.list().query(query + "&limit=0").expectBody("{\"total\":" + n + "}").ok();
    }


    public static final class IDFilter implements IColumnFilter {

        @Override
        public boolean accept(String tableName, Column column) {
            return !column.getColumnName()
                .equalsIgnoreCase("id");
        }
    }
}
