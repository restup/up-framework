package com.github.restup.controller;

import com.github.restup.controller.content.negotiation.ContentNegotiator;
import com.github.restup.controller.content.negotiation.NoOpContentNegotiator;
import com.github.restup.controller.interceptor.NoOpRequestInterceptor;
import com.github.restup.controller.interceptor.RequestInterceptor;
import com.github.restup.controller.model.HttpMethod;
import com.github.restup.controller.model.ParsedResourceControllerRequest;
import com.github.restup.controller.model.ResourceControllerRequest;
import com.github.restup.controller.model.ResourceControllerResponse;
import com.github.restup.controller.request.parser.RequestParser;
import com.github.restup.service.MethodCommandOperations;
import com.github.restup.service.ResourceServiceOperations;
import com.model.test.company.Company;
import com.model.test.company.Person;
import com.github.restup.errors.ErrorObjectException;
import com.github.restup.errors.RequestError;
import com.github.restup.registry.Resource;
import com.github.restup.registry.ResourceRegistry;
import com.github.restup.registry.TestRegistry;
import com.github.restup.service.FilteredService;
import com.github.restup.service.model.request.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.*;

@SuppressWarnings({"rawtypes", "unchecked"})
@RunWith(MockitoJUnitRunner.class)
public class ResourceControllerTest {

    @Mock
    private ResourceServiceOperations service;
    @Mock
    private ResourceControllerRequest request;
    @Mock
    private ResourceControllerResponse response;
    @Mock
    private RequestParser bodyParser;
    @Mock
    private RequestInterceptor interceptorA;
    @Mock
    private RequestInterceptor interceptorB;
    @Mock
    private ContentNegotiator contentNegotiatorA;
    @Mock
    private ContentNegotiator contentNegotiatorB;
    @Mock
    private ContentNegotiator contentNegotiatorC;
    private ResourceController controller;
    private List<String> params;
    private String expectedErrorCode;
    private int expectedHttpStatus = 400;

    public static void assertInvocations(int i, Object toInspect) {
        assertEquals(1, CollectionUtils.size(Mockito.mockingDetails(toInspect).getInvocations()));
    }

    public ResourceController controller(ResourceRegistry registry) {
        return ResourceController.builder()
                .requestParsers(bodyParser)
                .contentNegotiators(contentNegotiatorA, contentNegotiatorB, contentNegotiatorC, new NoOpContentNegotiator())
                .interceptors(interceptorA, interceptorB, new NoOpRequestInterceptor())
                .registry(registry)
                .requestParamParsers()
                .autoDetectDisabled(true)
                .build();
    }

    @Before
    public void setup() {
        ResourceRegistry registry = TestRegistry.registry();
        registry.registerResource(Resource.builder(Company.class).service(service));
        registry.registerResource(Resource.builder(Person.class).name("person"));
        controller = controller(registry);
        when(request.getResource()).thenReturn((Resource) registry.getResource(Company.class));
    }

    private void error(String code) {
        error(400, code);
    }

    private void error(int status, String code) {
        this.expectedErrorCode = code;
        this.expectedHttpStatus = status;
    }

    private void relationship() {
        when(request.getRelationship()).thenReturn(Mockito.mock(Resource.class));
    }

    private void body(final Object data) {
        doAnswer(new Answer() {
            public Object answer(InvocationOnMock invocation) throws Throwable {
                ParsedResourceControllerRequest.Builder b = (ParsedResourceControllerRequest.Builder) invocation.getArguments()[1];
                if (data instanceof Object[]) {
                    b.setData(Arrays.asList((Object[]) data));
                } else {
                    b.setData(data);
                }
                return null;
            }
        }).when(bodyParser).parse(any(ResourceControllerRequest.class), any(ParsedResourceControllerRequest.Builder.class));
    }

    private void get(Integer... ids) {
        request(HttpMethod.GET, ids);
    }

    private void delete(Integer... ids) {
        request(HttpMethod.DELETE, ids);
    }

    private void post(Object... data) {
        body(data);
        request(HttpMethod.POST);
    }

    private void post(Integer... id) {
        request(HttpMethod.POST, id);
    }

    private void put(Object... data) {
        body(data);
        put();
    }

    private void put(Integer... ids) {
        request(HttpMethod.PUT, ids);
    }

    private void patch(Object... body) {
        patchById((Object) body);
    }

    private void patchById(Object body, Integer... ids) {
        body(body);
        request(HttpMethod.PATCH, ids);
    }

    private void page(int offset, int limit) {
        param("offset", String.valueOf(offset));
        param("limit", String.valueOf(limit));
    }

    private void fields(String... fields) {
        param("fields", fields);
    }

    private void filter(String field, String value) {
        param("filter[" + field + "]", value);
    }

    private void include(String... fields) {
        param("query", fields);
    }

    private void sort(String... fields) {
        param("sort", fields);
    }

    private void param(String name, final String... values) {
        if (params == null) {
            params = new ArrayList<String>();
        }
        params.add(name);
        when(request.getParameter(name)).thenReturn(values);
    }

    private void request(HttpMethod method, Integer... ids) {
        when(request.getMethod()).thenReturn(method);
        if (ArrayUtils.isNotEmpty(ids)) {
            when(request.getIds()).thenReturn((List) Arrays.asList(ids));
        }
        when(request.getParameterNames()).thenReturn(params);
        when(contentNegotiatorA.accept(any(ResourceControllerRequest.class))).thenReturn(true);
        try {
            controller.request(request, response);
        } catch (ErrorObjectException e) {
            RequestError error = e.getErrors().iterator().next();
            assertEquals(expectedErrorCode, error.getCode());
            assertEquals(expectedHttpStatus, error.getHttpStatus());
            throw e;
        }
        assertInvocations(1, service);
        verify(interceptorA, times(1)).before(any(ParsedResourceControllerRequest.class));
        verify(interceptorA, times(1)).before(any(ParsedResourceControllerRequest.class));
        verify(interceptorB, times(1)).after(any(ParsedResourceControllerRequest.class));
        verify(interceptorB, times(1)).after(any(ParsedResourceControllerRequest.class));
        verify(contentNegotiatorA, times(1)).accept(any(ResourceControllerRequest.class));
        verify(contentNegotiatorA, times(1)).formatResponse(any(ParsedResourceControllerRequest.class), any(ResourceControllerResponse.class), nullable(Object.class));
        verify(contentNegotiatorB, times(0)).formatResponse(any(ParsedResourceControllerRequest.class), any(ResourceControllerResponse.class), nullable(Object.class));
    }

    @Test
    public void testPost() {
        fields("name,id", "workers");
        post(company());
        verify(service, times(1)).create(any(CreateRequest.class));
    }

    @Test
    public void testPostMultiple() {
        post(company(), company());
        verify(service, times(1)).create(any(BulkRequest.class));
    }

    @Test(expected = ErrorObjectException.class)
    public void testPostItemResourceError() {
        error(405, "METHOD_NOT_ALLOWED");
        post(1);
    }

    @Test(expected = ErrorObjectException.class)
    public void testPostErrorCollectionResourceError() {
        error(405, "METHOD_NOT_ALLOWED");
        post(1, 2, 3);
    }

    @Test(expected = ErrorObjectException.class)
    public void testPostErrorDataRequired() {
        error("DATA_REQUIRED");
        post();
    }

    @Test
    public void testPut() {
        fields("name,id");
        body(company());
        put(1);
        verify(service, times(1)).update(any(UpdateRequest.class));
    }

    @Test
    public void testPutMultiple() {
        put(company(), company());
        verify(service, times(1)).update(any(BulkRequest.class));
    }

    @Test
    public void testGet() {
        get(1);
        verify(service, times(1)).find(any(ReadRequest.class));
    }

    @Test
    public void testList() {
        filter("name", "boo");
        sort("name");
        fields("name,workers");
        include("person");
        page(1, 2);
        get();
        verify(service, times(1)).list(any(ListRequest.class));
    }

    @Test
    public void testGetByIds() {
        fields("name,workers");
        include("person");
        get(1, 2, 3);
        verify(service, times(1)).list(any(ListRequest.class));
    }

    @Test(expected = ErrorObjectException.class)
    public void testGetByIdsRelationship() {
        error("RELATIONSHIP_IDS_NOT_SUPPORTED");
        relationship();
        get(1, 2, 3);
    }

    @Test
    public void testDelete() {
        fields("name,id");
        delete(1);
        verify(service, times(1)).delete(any(DeleteRequest.class));
    }

    @Test
    public void testDeleteByIds() {
        delete(1, 2, 3);
        verify(service, times(1)).delete(any(BulkRequest.class));
    }

    @Test
    public void testDeleteByFilter() {
        filter("name", "boo");
        delete();
        verify(service, times(1)).deleteByQueryCriteria(any(DeleteRequest.class));
    }

    @Test
    public void testPatch() {
        patchById(company(), 1);
        verify(service, times(1)).update(any(UpdateRequest.class));
    }

    @Test
    public void testPatchMultipleDocuments() {
        patch(company(), company());
        verify(service, times(1)).update(any(BulkRequest.class));
    }

    @Test
    public void testPatchByIds() {
        patchById(company(), 1, 2, 3);
        verify(service, times(1)).updateByQueryCriteria(any(UpdateRequest.class));
    }

    @Test
    public void testPatchByFilter() {
        filter("name", "boo");
        patch(company());
        verify(service, times(1)).updateByQueryCriteria(any(UpdateRequest.class));
    }

    private Company company() {
        return new Company();
    }

}
