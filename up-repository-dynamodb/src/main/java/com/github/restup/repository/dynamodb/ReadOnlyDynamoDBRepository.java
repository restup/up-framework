package com.github.restup.repository.dynamodb;

import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
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
import com.github.restup.mapping.fields.MappedField;
import com.github.restup.mapping.fields.MappedIndexField;
import com.github.restup.query.Pagination;
import com.github.restup.query.PreparedResourceQueryStatement;
import com.github.restup.query.ResourceSort;
import com.github.restup.query.criteria.ResourcePathFilter;
import com.github.restup.query.criteria.ResourcePathFilter.Operator;
import com.github.restup.registry.Resource;
import com.github.restup.service.model.request.ReadRequest;
import com.github.restup.service.model.response.PagedResult;
import com.github.restup.service.model.response.ReadResult;
import com.github.restup.util.UpUtils;
import com.google.common.collect.ImmutableSet;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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

        if (optimized.hasKeyCriteria()) {
            // query by partition key using in not allowed
            List<T> result = listWithInKeyCriteria(resource, optimized);
            if (isNotEmpty(result)) {
                return PagedResult.of(result, ps.getPagination(), Long.valueOf(result.size()));
            }
        }
        if (!optimized.hasIndexCriteria()) {
            if (resource.getMapping().isIndexedQueryOnly()) {
                throw new UnsupportedOperationException("Insufficient index criteria");
            }
            return scan(ps, optimized);
        }

        ExpressionBuilder expressionBuilder = new ExpressionBuilder();

        DynamoDBQueryExpression<T> expression = new DynamoDBQueryExpression<>();
        if (optimized.hasKeyCriteria()) {
            expressionBuilder.addCriteria(optimized.getKeyCriteria());
            expression.withKeyConditionExpression(expressionBuilder.getExpression());
            expressionBuilder.addCriteria(optimized.getIndexCriteria());
        } else {
            expression.setIndexName(optimized.getIndexName());
            expression.setConsistentRead(false); // not permitted with GSI
            expressionBuilder.addCriteria(optimized.getIndexCriteria());
            expression.withKeyConditionExpression(expressionBuilder.getExpression());
        }

        expressionBuilder.addCriteria(optimized.getFilterCriteria());

        String filter = expressionBuilder.getExpression();
        if (isNotBlank(filter)) {
            expression.withFilterExpression(filter);
        }

        expression.withExpressionAttributeValues(expressionBuilder.getAttributeValues());

        sort(expression, ps, optimized);

        // apply paging if required
        List<T> list = null;
        Long totalCount = null;
        Pagination pagination = ps.getPagination();
        Class<T> resourceClass = resource.getClassType();
        if (!pagination.isPagingEnabled()) {
            pagination = null;
            list = mapper.query(resourceClass, expression);
        } else {
            Integer limit = pagination.getLimit();
            expression.withLimit(limit);

            if (isNotEmpty(pagination.getKey())) {
                expression.setExclusiveStartKey(toKey(pagination.getKey()));
            }

            QueryResultPage page = mapper.queryPage(resourceClass, expression);
            list = page.getResults();
            pagination = Pagination.of(limit, fromKey(page.getLastEvaluatedKey()));
        }
        return PagedResult.of(list, pagination, totalCount);
    }

    private List<T> listWithInKeyCriteria(Resource resource,
        OptimizedResourceQueryCriteria optimized) {
        Class<T> resourceClass = resource.getClassType();
        List<T> result = new ArrayList<>();

        boolean otherOperatorsExist = false;
        for (ResourcePathFilter f : optimized.getKeyCriteria()) {
            //  dynamo does not permit in with key
            if (Objects.equals(f.getOperator(), Operator.in)) {
                Collection collection = UpUtils.asCollection(f.getValue());
                for (Object id : collection) {
                    T item = findOne(resourceClass, id);
                    UpUtils.addIfNotNull(result, item);
                }
            } else if (Objects.equals(f.getOperator(), Operator.eq)) {
                T item = findOne(resourceClass, f.getValue());
                UpUtils.addIfNotNull(result, item);
            } else {
                otherOperatorsExist = true;
            }
        }

        if (otherOperatorsExist && result.size() > 0) {
            throw new UnsupportedOperationException(
                "Unable to combine operators for primary key");
        }
        return result;
    }

    private void sort(DynamoDBQueryExpression<T> expression, PreparedResourceQueryStatement ps,
        OptimizedResourceQueryCriteria optimized) {

        if (ps.getRequestedSort() != null) {
            for (ResourceSort sort : ps.getRequestedSort()) {
                MappedField mf = sort.getPath().lastMappedField();
                MappedIndexField mappedIndexField = getIndex(optimized.getIndexName(),
                    mf.getIndexes());
                if (mappedIndexField == null) {
                    throw new UnsupportedOperationException("Sort invalid for selected criteria");
                }

                expression.withScanIndexForward(sort.isAscending());
            }

        }
    }

    private MappedIndexField getIndex(String indexName, Collection<MappedIndexField> indexes) {
        if (indexes != null) {
            return indexes.stream()
                .filter((idx) -> Objects.equals(indexName, idx.getIndexName()))
                .findFirst().get();
        }
        return null;
    }

    private PagedResult<T> scan(PreparedResourceQueryStatement ps,
        OptimizedResourceQueryCriteria optimized) {

        if (isNotEmpty(ps.getRequestedSort())) {
            throw new UnsupportedOperationException("Sort not allowed with provided filters");
        }
        ExpressionBuilder expressionBuilder = new ExpressionBuilder();
        expressionBuilder.addCriteria(optimized.getFilterCriteria());

        DynamoDBScanExpression expression = new DynamoDBScanExpression();

        String filter = expressionBuilder.getExpression();
        if (isNotEmpty(filter)) {
            expression.withFilterExpression(filter)
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

            Integer limit = pagination.getLimit();
            expression.withLimit(limit);

            if (isNotEmpty(pagination.getKey())) {
                expression.setExclusiveStartKey(toKey(pagination.getKey()));
            }

            ScanResultPage page = mapper.scanPage(resourceClass, expression);
            list = page.getResults();
            pagination = Pagination.of(limit, fromKey(page.getLastEvaluatedKey()));
        }
        return PagedResult.of(list, pagination, totalCount);
    }


}
