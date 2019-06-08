package com.github.restup.repository.dynamodb;

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.QueryResultPage;
import com.amazonaws.services.dynamodbv2.datamodeling.ScanResultPage;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.github.restup.annotations.operations.ListResource;
import com.github.restup.annotations.operations.ReadResource;
import com.github.restup.query.Pagination;
import com.github.restup.query.PreparedResourceQueryStatement;
import com.github.restup.query.criteria.ResourcePathFilter.Operator;
import com.github.restup.registry.Resource;
import com.github.restup.service.model.request.ReadRequest;
import com.github.restup.service.model.response.PagedResult;
import com.github.restup.service.model.response.ReadResult;
import com.google.common.collect.ImmutableSet;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReadOnlyDynamoDBRepository<T, ID extends Serializable> {

    private final static Logger log = LoggerFactory.getLogger(ReadOnlyDynamoDBRepository.class);
    private final DynamoDBMapper mapper;
    private final Set<Operator> collectionValues;

    public ReadOnlyDynamoDBRepository(DynamoDBMapper mapper) {
        this.mapper = mapper;
        collectionValues = ImmutableSet.of(Operator.in, Operator.nin);
    }

    static Map<String, AttributeValue> toKey(String key) {
        return null;
    }

    static String fromKey(Map lastEvaluatedKey) {
        return null;
    }

    boolean supportsCollection(Operator operator) {
        return collectionValues.contains(operator);
    }

    public DynamoDBMapper getMapper() {
        return mapper;
    }

    @ReadResource
    public ReadResult<T> find(ReadRequest<T, ID> request) {
        T t = findOne((Resource<T, ID>) request.getResource(), request.getId());
        return ReadResult.of(t);
    }

    protected T findOne(Resource<T, ID> resource, ID id) {
        return findOne(resource.getClassType(), id);
    }

    protected T findOne(Class<T> resourceClass, Object id) {
        DynamoDBMapper ddb = getMapper();
        return ddb.load(resourceClass, id);
    }

    @ListResource
    public PagedResult<T> list(PreparedResourceQueryStatement ps) {
        Resource resource = ps.getResource();

        OptimizedResourceQueryCriteria optimized = OptimizedResourceQueryCriteria.builder().add(ps)
            .build();

        // query in by partition key not allows
        if (!optimized.hasIndexCriteria()) {
            if (resource.getMapping().isIndexedQueryOnly()) {
                throw new UnsupportedOperationException("Insufficient index criteria");
            }
            return scan(ps, optimized);
        }

        ExpressionBuilder expressionBuilder = new ExpressionBuilder();

        DynamoDBQueryExpression<T> expression = new DynamoDBQueryExpression<>();

        expressionBuilder.addCriteria(optimized.getIndexCriteria());
        expression.withKeyConditionExpression(expressionBuilder.getExpression());

        expressionBuilder.addCriteria(optimized.getFilterCriteria());

        String filter = expressionBuilder.getExpression();
        if (isNotBlank(filter)) {
            expression.withFilterExpression(filter);
        }

        expression.withExpressionAttributeValues(expressionBuilder.getAttributeValues());

        expression.withScanIndexForward(true);

//
//        // build & apply sort
//        List<Order> order = sort(ps, cb, root);
//        sort(q, order);
//
        // apply paging if required
        List<T> list = null;
        Long totalCount = null;
        Pagination pagination = ps.getPagination();
        Class<T> resourceClass = resource.getClassType();
        if (!pagination.isPagingEnabled()) {
            pagination = null;
            list = mapper.query(resourceClass, expression);
        } else {
//            if (pagination.isWithTotalsEnabled()) {
//                totalCount = count(resourceClass, predicates);
//                log.debug("{} count({}); {}", resource, query, totalCount);
//            }

            Integer limit = pagination.getLimit();
//            if (Pagination.isPagedListRequired(pagination, totalCount)) {
//                int start = Pagination.getStart(pagination);
            expression.withLimit(limit);

            if (isNotEmpty(pagination.getKey())) {
                expression.setExclusiveStartKey(toKey(pagination.getKey()));
            }

            QueryResultPage page = mapper.queryPage(resourceClass, expression);
            list = page.getResults();
            pagination = Pagination.of(limit, fromKey(page.getLastEvaluatedKey()));
//            } else if (limit > 0) {
//                list = Collections.EMPTY_LIST;
//            } else {
//                pagination = null;
//            }
        }
        return PagedResult.of(list, pagination, totalCount);
    }

    private PagedResult<T> scan(PreparedResourceQueryStatement ps,
        OptimizedResourceQueryCriteria optimized) {
        ExpressionBuilder expressionBuilder = new ExpressionBuilder();
        expressionBuilder.addCriteria(optimized.getFilterCriteria());

        DynamoDBScanExpression expression = new DynamoDBScanExpression();

        String filter = expressionBuilder.getExpression();
        if (isNotEmpty(filter)) {
            expression.withFilterExpression(expressionBuilder.getExpression())
                .withExpressionAttributeValues(expressionBuilder.getAttributeValues());
        }

        // apply paging if required
        List<T> list = null;
        Long totalCount = null;
        Pagination pagination = ps.getPagination();
        Resource resource = ps.getResource();
        Class<T> resourceClass = resource.getClassType();
        if (!pagination.isPagingEnabled()) {
            pagination = null;
            list = mapper.scan(resourceClass, expression);
        } else {
//            if (pagination.isWithTotalsEnabled()) {
//                totalCount = count(resourceClass, predicates);
//                log.debug("{} count({}); {}", resource, query, totalCount);
//            }

            Integer limit = pagination.getLimit();
//            if (Pagination.isPagedListRequired(pagination, totalCount)) {
            expression.withLimit(limit);

            if (isNotEmpty(pagination.getKey())) {
                expression.setExclusiveStartKey(toKey(pagination.getKey()));
            }

            ScanResultPage page = mapper.scanPage(resourceClass, expression);
            list = page.getResults();
            pagination = Pagination.of(limit, fromKey(page.getLastEvaluatedKey()));
//            } else if (limit > 0) {
//                list = Collections.EMPTY_LIST;
//            } else {
//                pagination = null;
//            }
        }
        return PagedResult.of(list, pagination, totalCount);
    }


}
