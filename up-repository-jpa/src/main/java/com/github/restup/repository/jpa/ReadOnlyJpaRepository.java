package com.github.restup.repository.jpa;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.github.restup.annotations.operations.ListResource;
import com.github.restup.annotations.operations.ReadResource;
import com.github.restup.bind.converter.StringToBooleanConverter;
import com.github.restup.mapping.fields.MappedField;
import com.github.restup.path.ResourcePath;
import com.github.restup.query.Pagination;
import com.github.restup.query.PreparedResourceQueryStatement;
import com.github.restup.query.ResourceSort;
import com.github.restup.query.criteria.AndCriteria;
import com.github.restup.query.criteria.OrCriteria;
import com.github.restup.query.criteria.ResourcePathFilter;
import com.github.restup.query.criteria.ResourcePathFilter.Operator;
import com.github.restup.query.criteria.ResourceQueryCriteria;
import com.github.restup.registry.Resource;
import com.github.restup.service.model.request.ReadRequest;
import com.github.restup.service.model.response.PagedResult;
import com.github.restup.service.model.response.ReadResult;

public class ReadOnlyJpaRepository<T, ID extends Serializable> {

    private final static Logger log = LoggerFactory.getLogger(ReadOnlyJpaRepository.class);
    private final EntityManager entityManager;

    public ReadOnlyJpaRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    private static void filter(CriteriaQuery<?> q, CriteriaBuilder cb, List<Predicate> predicates) {
        if (!predicates.isEmpty()) {
            q.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
        }
    }

    protected static <T> List<Order> sort(PreparedResourceQueryStatement query, CriteriaBuilder cb, Root<T> root) {
        List<Order> result = null;
        List<ResourceSort> sortFields = query.getRequestedSort();
        if (sortFields != null) {
            result = new ArrayList<Order>();
            for (ResourceSort f : sortFields) {
                ResourcePath path = f.getPath();
                MappedField<?> field = path.lastMappedField();
                if (field != null) {
                    String sortFieldName = path.getPersistedPath();
                    String caseInsensitiveSortFieldName = field.getCaseInsensitiveSearchField();

                    if (StringUtils.isNotEmpty(caseInsensitiveSortFieldName)) {
                        result.add(sort(f, cb, root, caseInsensitiveSortFieldName));
                    }

                    result.add(sort(f, cb, root, sortFieldName));
                }

            }
        }
        return result;
    }

    private static <T> Order sort(ResourceSort f, CriteriaBuilder cb, Root<T> root, String sortFieldName) {
        if (f.isAscending()) {
            return cb.asc(root.get(sortFieldName));
        } else {
            return cb.desc(root.get(sortFieldName));
        }
    }

    private static <T> void sort(CriteriaQuery<T> q, List<Order> order) {
        if (CollectionUtils.isNotEmpty(order)) {
            q.orderBy(order.toArray(new Order[]{}));
        }
    }

    protected EntityManager getEntityManager() {
        return entityManager;
    }

    @SuppressWarnings("unchecked")
    @ReadResource
    public ReadResult<T> find(ReadRequest<T, ID> request) {
        T t = findOne((Resource<T, ID>) request.getResource(), request.getId());
        return ReadResult.of(t);
    }

    protected T findOne(Resource<T, ID> resource, ID id) {
        return findOne(resource.getClassType(), id);
    }

    protected T findOne(Class<T> resourceClass, Object id) {
        EntityManager em = getEntityManager();
        return em.find(resourceClass, id);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @ListResource
    public PagedResult<T> list(PreparedResourceQueryStatement ps) {
        EntityManager em = getEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        Resource resource = ps.getResource();
        Class<T> resourceClass = resource.getClassType();
        CriteriaQuery<T> q = cb.createQuery(resourceClass);

        Root<T> root = q.from(resourceClass);
        q.select(root);

        // build & apply predicates
        List<Predicate> predicates = buildPredicates(ps, cb, root);
        filter(q, cb, predicates);

        // build & apply sort
        List<Order> order = sort(ps, cb, root);
        sort(q, order);

        TypedQuery<T> query = em.createQuery(q);

        // apply paging if required
        List<T> list = null;
        Long totalCount = null;
        Pagination pagination = ps.getPagination();
        if (!pagination.isPagingEnabled()) {
            pagination = null;
            list = query.getResultList();
        } else {
            if (pagination.isWithTotalsEnabled()) {
                totalCount = count(resourceClass, predicates);
                log.debug("{} count({}); {}", resource, query, totalCount);
            }

            Integer limit = pagination.getLimit();
            if (Pagination.isPagedListRequired(pagination, totalCount)) {
                int start = Pagination.getStart(pagination);

                query.setFirstResult(start);
                query.setMaxResults(limit);

                list = query.getResultList();
            } else if (limit > 0) {
                list = Collections.EMPTY_LIST;
            } else {
                pagination = null;
            }
        }
        return PagedResult.of(list, pagination, totalCount);
    }

    private Long count(Class<T> resourceClass, List<Predicate> predicates) {
        EntityManager em = getEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> tot = cb.createQuery(Long.class);

        tot.select(cb.count(tot.from(resourceClass)));

        filter(tot, cb, predicates);

        return em.createQuery(tot).getSingleResult();
    }

    private List<Predicate> buildPredicates(PreparedResourceQueryStatement query, CriteriaBuilder cb, Root<T> root) {
        List<Predicate> predicates = new ArrayList<Predicate>();
        List<ResourceQueryCriteria> list = query.getRequestedCriteria();

        Predicate predicate = buildPredicate(list, cb, root, false);
        if (predicate != null) {
            predicates.add(predicate);
        }
        return predicates;
    }

    @SuppressWarnings("rawtypes")
    private Predicate buildPredicate(List<ResourceQueryCriteria> list, CriteriaBuilder cb, Root<T> root, boolean or) {
        if (list != null) {
            List<Predicate> criteria = new ArrayList<Predicate>();
            for (ResourceQueryCriteria c : list) {

                if (c instanceof ResourcePathFilter) {
                    ResourcePathFilter<?> f = (ResourcePathFilter) c;
                    String key = f.getPath().getPersistedPath();

                    Operator operator = f.getOperator();
                    Object value = f.getValue();

                    MappedField<?> mf = f.getPath().lastMappedField();

                    String lowerCaseFieldName = mf.getCaseInsensitiveSearchField();
                    if (StringUtils.isNotEmpty(lowerCaseFieldName)) {
                        // if there is a lowerCaseFieldName specified, lowercase the
                        // value and set caseInsensitive to false treat it as a normal query
                        key = lowerCaseFieldName;
                        value = MappedField.toCaseInsensitive(mf.getCaseSensitivity(), value);
                    }

                    if (value instanceof Collection && !supportsCollection(operator)) {
                        for (Object o : (Collection) value) {
                            add(criteria, criteria(cb, root, key, operator, o, f));
                        }
                    } else {
                        add(criteria, criteria(cb, root, key, operator, value, f));
                    }
                } else if (c instanceof AndCriteria) {
                    add(criteria, buildPredicate(((AndCriteria) c).getCriteria(), cb, root, false));
                } else if (c instanceof OrCriteria) {
                    add(criteria, buildPredicate(((OrCriteria) c).getCriteria(), cb, root, true));
                }
            }

            if (criteria.size() == 1) {
                if (or) {
                    return cb.or(criteria.get(0));
                } else {
                    return cb.and(criteria.get(0));
                }
            } else if (criteria.size() > 1) {
                Predicate[] arr = criteria.toArray(new Predicate[0]);
                if (or) {
                    return cb.or(arr);
                } else {
                    return cb.and(arr);
                }
            }
        }
        return null;
    }

    private <R> void add(List<R> list, R o) {
        if (o != null) {
            list.add(o);
        }
    }

    @SuppressWarnings("incomplete-switch")
    private boolean supportsCollection(Operator operator) {
        switch (operator) {
            case in:
                return true;
        }
        return false;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private Predicate criteria(CriteriaBuilder cb, Root<T> root, String key,
            ResourcePathFilter.Operator operator, Object value, ResourcePathFilter<?> f) {
        Expression<?> keyPath = root.get(key);

        switch (operator) {
            case eq:
                if (value instanceof Collection) {
                    return keyPath.in(cb.literal(value));
                }
                return cb.equal(keyPath, cb.literal(value));
            case like:
                if (value instanceof String) {
                    String s = (String) value;
                    return cb.like((Expression) keyPath, (Expression) cb.literal(s.replaceAll("\\*", "%")));
                }
                return cb.like((Expression) keyPath, (Expression) cb.literal(value));
            case ne:
                if (value instanceof Collection) {
                    return keyPath.in(cb.literal(value)).not();
                }
                return cb.notEqual(keyPath, cb.literal(value));
            case in:
                return keyPath.in(cb.literal(value));
            case nin:
                return keyPath.in(cb.literal(value)).not();
            case gt:
                return cb.gt((Expression) keyPath, (Expression) cb.literal(value));
            case lt:
                return cb.lt((Expression) keyPath, (Expression) cb.literal(value));
            case lte:
                return cb.le((Expression) keyPath, (Expression) cb.literal(value));
            case gte:
                return cb.ge((Expression) keyPath, (Expression) cb.literal(value));
            case exists:
                if (StringToBooleanConverter.isTrue(value)) {
                    return cb.isNotNull(keyPath);
                } else {
                    return cb.isNull(keyPath);
                }
            case regex:
                // RequestError.builder()
                // .setCode()
                // ErrorObject.notSupported("regex is not a supported filter
                // function").param(f.getRawParam())
                // .throwException();
        }
        return null;
    }
}
