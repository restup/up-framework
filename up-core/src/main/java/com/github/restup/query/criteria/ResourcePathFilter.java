package com.github.restup.query.criteria;

import com.github.restup.mapping.fields.MappedField;
import com.github.restup.path.ResourcePath;
import com.github.restup.registry.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Represents query criteria for a path
 */
public class ResourcePathFilter<T> implements ResourceQueryCriteria {

    private final ResourcePath path;
    private final Operator operator;
    private final T value;

    public ResourcePathFilter(ResourcePath path, T value) {
        this(path, Operator.eq, value);
    }

    public ResourcePathFilter(ResourcePath path, Operator operator, T value) {
        super();
        this.path = path;
        this.operator = operator;
        this.value = value;
    }

    public ResourcePathFilter(Resource<?, ?> resource, String field, Operator operator, T value) {
        this(ResourcePath.path(resource, field), operator, value);
    }

    public ResourcePathFilter(Resource<?, ?> resource, String field, T value) {
        this(ResourcePath.path(resource, field), value);
    }

    private static boolean compare(Operator operator, Comparable<Object> a, Object b) {
        switch (operator) {
            case eq:
                return a.compareTo(b) == 0;
            case ne:
                return a.compareTo(b) != 0;
            case gt:
                return a.compareTo(b) > 0;
            case gte:
                return a.compareTo(b) > 0;
            case lt:
                return a.compareTo(b) < 0;
            case lte:
                return a.compareTo(b) <= 0;
            case in:
                if (b instanceof Collection) {
                    return ((Collection<?>) b).contains(a);
                } else {
                    return compare(Operator.eq, a, b);
                }
            case nin:
                if (b instanceof Collection) {
                    return !((Collection<?>) b).contains(a);
                } else {
                    return compare(Operator.ne, a, b);
                }
            case exists:
            case regex:
            case like:
                throw new UnsupportedOperationException(operator + " not supported");
        }
        return false;
    }

    public ResourcePath getPath() {
        return path;
    }

    public Operator getOperator() {
        return operator;
    }

    public T getValue() {
        return value;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
    public boolean filter(Object t) {
        MappedField<?> mf = path.lastMappedField();
        Comparable<?> a = (Comparable<?>) path.getValue(t);
        Object b = this.value;

        if (MappedField.isCaseInsensitive(mf)) {
            a = (Comparable<?>) MappedField.toCaseInsensitive(mf.getCaseSensitivity(), a);
            if (b instanceof Collection) {
                List<Object> result = new ArrayList<>(((Collection<?>) b).size());
                for (Object o : (Collection<?>) b) {
                    result.add(MappedField.toCaseInsensitive(mf.getCaseSensitivity(), o));
                }
                b = result;
            } else {
                b = MappedField.toCaseInsensitive(mf.getCaseSensitivity(), b);
            }
        }

        return compare(operator, (Comparable) a, b);
    }

    public enum Operator {
        gt(">"), lt("<"), lte("<="), gte(">="), ne("!="), eq("=", "is"), in("in"), nin("nin"), exists("exists"), regex("regex"), like("like");

        private final String[] operators;

        private Operator(String... operators) {
            this.operators = operators;
        }

        public static Operator of(String operator) {
            if (operator == null) {
                return Operator.eq;
            }
            for (Operator o : Operator.values()) {
                if (operator.equalsIgnoreCase(o.name())) {
                    return o;
                }
                for (String s : o.operators) {
                    if (operator.equalsIgnoreCase(s)) {
                        return o;
                    }
                }
            }
            return null;
        }

        String[] getOperators() {
            return operators;
        }
    }

}
