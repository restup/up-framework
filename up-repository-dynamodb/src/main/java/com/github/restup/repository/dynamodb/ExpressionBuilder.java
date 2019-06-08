package com.github.restup.repository.dynamodb;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.github.restup.bind.converter.StringToBooleanConverter;
import com.github.restup.mapping.fields.MappedField;
import com.github.restup.query.criteria.ResourcePathFilter;
import com.github.restup.query.criteria.ResourcePathFilter.Operator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

public class ExpressionBuilder {

    private final List<String> expressions = new ArrayList<>();
    private final Map<String, AttributeValue> attributeValues = new HashMap<>();


    static AttributeValue withValue(Object value) {
        AttributeValue attributeValue = new AttributeValue();
        if (value instanceof String) {
            return attributeValue.withS((String) value);
        } else if (value instanceof Number) {
            return attributeValue.withN(value.toString());
        } else if (value instanceof Boolean) {
            return attributeValue.withBOOL((Boolean) value);
        } else if (value == null) {
            return attributeValue.withNULL(true);
        }
        throw new UnsupportedOperationException("Not implemented");
    }

    static String criteria(String... tokens) {
        return StringUtils.join(tokens, " ");
    }

    static String nextBindArg(Map<String, AttributeValue> attributeValues) {
        return ":val" + attributeValues.size();
    }

    public String getExpression() {
        String expression = StringUtils.join(expressions, " and ");
        expressions.clear();
        return expression;
    }

    public Map<String, AttributeValue> getAttributeValues() {
        return attributeValues;
    }

    public void addCriteria(Collection<ResourcePathFilter> c) {
        if (c != null) {
            c.stream().forEach(this::addCriteria);
        }
    }

    public void addCriteria(ResourcePathFilter f) {
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
        addCriteria(key, operator, value, f);
    }

    private void addCriteria(String key, ResourcePathFilter.Operator operator, Object value,
        ResourcePathFilter<?> f) {

//        Map<ResourcePathFilter.Operator, BiC>

        switch (operator) {
//            case like:
//                if (value instanceof String) {
//                    String s = (String) value;
//                    return cb.like((Expression) keyPath,
//                        (Expression) cb.literal(s.replaceAll("\\*", "%")));
//                }
//                // contains (Brand, :v_sub)
//                return cb.like((Expression) keyPath, (Expression) cb.literal(value));
            case ne:
                if (value instanceof Collection) {
                    for (Object item : (Collection) value) {
                        addExpression(key, "<>", value);
                    }
                    return;
                }
                addExpression(key, "<>", value);
                return;
            case in:
            case eq:
                if (value instanceof Collection) {
                    if (CollectionUtils.size(value) == 1) {
                        value = CollectionUtils.get(value, 0);
                    } else {
                        List<String> bindArgs = new ArrayList<>();
                        for (Object item : (Collection) value) {
                            String bindArg = nextBindArg(attributeValues);
                            attributeValues.put(bindArg, withValue(item));
                            bindArgs.add(bindArg);
                        }
                        StringBuilder sb = new StringBuilder();
                        sb.append(key).append(" IN (").append(StringUtils.join(bindArgs, ","))
                            .append(")");
                        addExpression(sb.toString());
                        return;
                    }
                }
                addExpression(key, "=", value);
                return;
//            case nin:
//                return keyPath.in(cb.literal(value)).not();
            case gt:
                addExpression(key, ">", value);
                return;
            case lt:
                addExpression(key, "<", value);
                return;
            case lte:
                addExpression(key, "<=", value);
                return;
            case gte:
                addExpression(key, ">=", value);
                return;
            case exists:
                if (StringToBooleanConverter.isTrue(value)) {
                    addExpression(StringUtils.join("attribute_exists (", key, ")"));
                } else {
                    addExpression(StringUtils.join("attribute_not_exists (", key, ")"));
                }
            case regex:

                // RequestError.builder()
                // .setCode()
                // ErrorObject.notSupported("regex is not a supported filter
                // function").param(f.getRawParam())
                // .throwException();
        }
    }

    public void addExpression(String key,
        String operator, Object value) {
        addExpression(key, operator, withValue(value));
    }

    public void addExpression(String key,
        String operator, AttributeValue value) {
        String bindArg = nextBindArg(attributeValues);
        addExpression(criteria(key, operator, bindArg));
        attributeValues.put(bindArg, value);
    }


    public void addExpression(String expression) {
        expressions.add(expression);
    }


}
