package com.github.restup.repository.dynamodb;

import static org.apache.commons.collections4.CollectionUtils.isEmpty;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.QueryResultPage;
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
import java.util.Collections;
import java.util.List;
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

    //    @ListResource
    public PagedResult<T> list(PreparedResourceQueryStatement ps) {
        Resource resource = ps.getResource();
        Class<T> resourceClass = resource.getClassType();

        OptimizedResourceQueryCriteria optimized = OptimizedResourceQueryCriteria.builder().add(ps)
            .build();

        if (isEmpty(optimized.getIndexCriteria())) {
            throw new UnsupportedOperationException("Insufficient index criteria");
        }

        ExpressionBuilder expressionBuilder = new ExpressionBuilder();
        expressionBuilder.addCriteria(optimized.getIndexCriteria());

        DynamoDBQueryExpression<T> queryExpression = new DynamoDBQueryExpression<T>()
            .withKeyConditionExpression(expressionBuilder.getExpression());

        expressionBuilder.addCriteria(optimized.getFilterCriteria());

        queryExpression.withFilterExpression(expressionBuilder.getExpression())
            .withExpressionAttributeValues(expressionBuilder.getAttributeValues());

//
//        // build & apply sort
//        List<Order> order = sort(ps, cb, root);
//        sort(q, order);
//
        // apply paging if required
        List<T> list = null;
        Long totalCount = null;
        Pagination pagination = ps.getPagination();
        if (!pagination.isPagingEnabled()) {
            pagination = null;
            list = mapper.query(resourceClass, queryExpression);
        } else {
//            if (pagination.isWithTotalsEnabled()) {
//                totalCount = count(resourceClass, predicates);
//                log.debug("{} count({}); {}", resource, query, totalCount);
//            }

            Integer limit = pagination.getLimit();
            if (Pagination.isPagedListRequired(pagination, totalCount)) {
                int start = Pagination.getStart(pagination);
                queryExpression.withLimit(limit);

                //TODO pagination
//                queryExpression.setExclusiveStartKey();

                QueryResultPage queryResultPage = mapper.queryPage(resourceClass, queryExpression);
                list = queryResultPage.getResults();
            } else if (limit > 0) {
                list = Collections.EMPTY_LIST;
            } else {
                pagination = null;
            }
        }
        return PagedResult.of(list, pagination, totalCount);
    }


}
