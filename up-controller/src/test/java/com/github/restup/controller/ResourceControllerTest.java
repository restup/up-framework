package com.github.restup.controller;

import static com.github.restup.util.TestRegistries.mapBackedRegistry;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.restup.controller.content.negotiation.ContentNegotiator;
import com.github.restup.controller.content.negotiation.NoOpContentNegotiator;
import com.github.restup.controller.interceptor.NoOpRequestInterceptor;
import com.github.restup.controller.interceptor.RequestInterceptor;
import com.github.restup.controller.model.HttpMethod;
import com.github.restup.controller.model.ParsedResourceControllerRequest;
import com.github.restup.controller.model.ResourceControllerRequest;
import com.github.restup.controller.model.ResourceControllerResponse;
import com.github.restup.controller.request.parser.RequestParser;
import com.github.restup.controller.request.parser.path.RequestPathParserResult;
import com.github.restup.errors.RequestErrorException;
import com.github.restup.registry.Resource;
import com.github.restup.registry.ResourceRegistry;
import com.github.restup.service.ResourceServiceOperations;
import com.github.restup.service.model.request.BulkRequest;
import com.github.restup.service.model.request.CreateRequest;
import com.github.restup.service.model.request.DeleteRequest;
import com.github.restup.service.model.request.ListRequest;
import com.github.restup.service.model.request.ReadRequest;
import com.github.restup.service.model.request.UpdateRequest;
import com.model.test.company.Company;
import com.model.test.company.Person;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

@RunWith(MockitoJUnitRunner.class)
public class ResourceControllerTest {

    @Mock
    private ResourceServiceOperations service;
    @Mock
    private ResourceControllerRequest request;
    @Mock
    private ResourceControllerResponse response;
    @Mock
    private RequestPathParserResult requestPathParserResult;
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
    @Mock
    private Resource resource;
    private ResourceController controller;
    private List<String> params;

    public static void assertInvocations(int i, Object toInspect) {
        assertEquals(1, CollectionUtils.size(Mockito.mockingDetails(toInspect).getInvocations()));
    }

    public ResourceController controller(ResourceRegistry registry) {
        return ResourceController.builder()
                .autoDetectDisabled(true)
                .requestParser(RequestParser.builder()
                        .requestParsers(bodyParser))
                .contentNegotiator(ContentNegotiator.builder()
                        .contentNegotiators(contentNegotiatorA, contentNegotiatorB, contentNegotiatorC, new NoOpContentNegotiator()))
                .interceptors(interceptorA, interceptorB, new NoOpRequestInterceptor())
                .registry(registry)
                .build();
    }

    @Before
    public void setup() {
        ResourceRegistry registry = mapBackedRegistry();
        registry.registerResource(Resource.builder(Company.class).service(service));
        registry.registerResource(Resource.builder(Person.class).name("person"));
        controller = controller(registry);
        when(requestPathParserResult.getResource())
            .thenReturn((Resource) registry.getResource(Company.class));
    }

    private void error(String code, ThrowingCallable f) {
        error(400, code, f);
    }

    private void error(int status, String code, ThrowingCallable f) {
        Throwable thrownException = catchThrowable(f);
        
        Assertions.assertThat(thrownException)
                .isInstanceOf(RequestErrorException.class)
                .hasFieldOrPropertyWithValue("code", code)
                .hasFieldOrPropertyWithValue("httpStatus", status)
                .hasNoCause();
    }

    private void relationship() {
        when(requestPathParserResult.getRelationship()).thenReturn(Mockito.mock(Resource.class));
    }

    private void body(Object data) {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) {
                ParsedResourceControllerRequest.Builder b = (ParsedResourceControllerRequest.Builder) invocation
                    .getArguments()[2];
                if (data instanceof Object[]) {
                    b.setData(Arrays.asList((Object[]) data));
                } else {
                    b.setData(data);
                }
                return null;
            }
        }).when(bodyParser)
            .parse(any(ResourceControllerRequest.class), any(RequestPathParserResult.class),
                any(ParsedResourceControllerRequest.Builder.class));
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
        patchById(body);
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

    private void param(String name, String... values) {
        if (params == null) {
            params = new ArrayList<>();
        }
        params.add(name);
        when(request.getParameter(name)).thenReturn(values);
    }
    
    

    private void request(HttpMethod method, Integer... ids) {
        when(request.getMethod()).thenReturn(method);
        if (ArrayUtils.isNotEmpty(ids)) {
            when(requestPathParserResult.getIds()).thenReturn((List) Arrays.asList(ids));
        }
        when(request.getParameterNames()).thenReturn(params);
        when(contentNegotiatorA.accept(any(ResourceControllerRequest.class))).thenReturn(true);

        controller.requestInternal(request, requestPathParserResult, response);
        
        assertInvocations(1, service);
        verify(interceptorA).before(any(ParsedResourceControllerRequest.class));
        verify(interceptorA).before(any(ParsedResourceControllerRequest.class));
        verify(interceptorB).after(any(ParsedResourceControllerRequest.class));
        verify(interceptorB).after(any(ParsedResourceControllerRequest.class));
        verify(contentNegotiatorA, times(2)).accept(any(ResourceControllerRequest.class));
        verify(contentNegotiatorA).formatResponse(any(ParsedResourceControllerRequest.class), any(ResourceControllerResponse.class), nullable(Object.class));
        verify(contentNegotiatorB, times(0)).formatResponse(any(ParsedResourceControllerRequest.class), any(ResourceControllerResponse.class), nullable(Object.class));
    }

    @Test
    public void testPost() {
        fields("name,id", "workers");
        post(company());
        verify(service).create(any(CreateRequest.class));
    }

    @Test
    public void testPostMultiple() {
        post(company(), company());
        verify(service).create(any(BulkRequest.class));
    }

    @Test
    public void testPostItemResourceError() {
        error(405, "METHOD_NOT_ALLOWED", () -> post(1));
    }

    @Test
    public void testPostErrorCollectionResourceError() {
        error(405, "METHOD_NOT_ALLOWED", () -> post(1, 2, 3));
    }

    @Test
    public void testPostErrorDataRequired() {
        error("DATA_REQUIRED", () -> post());
    }

    @Test
    public void testPut() {
        fields("name,id");
        body(company());
        put(1);
        verify(service).update(any(UpdateRequest.class));
    }

    @Test
    public void testPutMultiple() {
        put(company(), company());
        verify(service).update(any(BulkRequest.class));
    }

    @Test
    public void testGet() {
        get(1);
        verify(service).find(any(ReadRequest.class));
    }

    @Test
    public void testList() {
        filter("name", "boo");
        sort("name");
        fields("name,workers");
        include("person");
        page(1, 2);
        get();
        verify(service).list(any(ListRequest.class));
    }

    @Test
    public void testGetByIds() {
        fields("name,workers");
        include("person");
        get(1, 2, 3);
        verify(service).list(any(ListRequest.class));
    }

    @Test
    public void testGetByIdsRelationship() {
        relationship();
        error("RELATIONSHIP_IDS_NOT_SUPPORTED", () -> get(1, 2, 3));
    }

    @Test
    public void testDelete() {
        fields("name,id");
        delete(1);
        verify(service).delete(any(DeleteRequest.class));
    }

    @Test
    public void testDeleteByIds() {
        delete(1, 2, 3);
        verify(service).delete(any(BulkRequest.class));
    }

    @Test
    public void testDeleteByFilter() {
        filter("name", "boo");
        delete();
        verify(service).deleteByQueryCriteria(any(DeleteRequest.class));
    }

    @Test
    public void testPatch() {
        patchById(company(), 1);
        verify(service).update(any(UpdateRequest.class));
    }

    @Test
    public void testPatchMultipleDocuments() {
        patch(company(), company());
        verify(service).update(any(BulkRequest.class));
    }

    @Test
    public void testPatchByIds() {
        patchById(company(), 1, 2, 3);
        verify(service).updateByQueryCriteria(any(UpdateRequest.class));
    }

    @Test
    public void testPatchByFilter() {
        filter("name", "boo");
        patch(company());
        verify(service).updateByQueryCriteria(any(UpdateRequest.class));
    }

    private Company company() {
        return new Company();
    }

}
