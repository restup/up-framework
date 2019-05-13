package com.github.restup.repository.dynamodb;

import com.github.restup.mapping.fields.MappedField;
import com.github.restup.mapping.fields.MappedIndexField;
import com.github.restup.query.PreparedResourceQueryStatement;
import com.github.restup.query.criteria.ResourcePathFilter;
import com.github.restup.query.criteria.ResourceQueryCriteria;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public interface OptimizedResourceQueryCriteria {

    static Builder builder() {
        return new Builder();
    }

    List<ResourcePathFilter> getIndexCriteria();

    List<ResourcePathFilter> getFilterCriteria();

    class Builder {

        private final List<ResourceQueryCriteria> filters = new ArrayList<>();

        private Builder() {

        }

        private Builder me() {
            return this;
        }

        public Builder add(PreparedResourceQueryStatement statement) {
            return addAll(statement.getRequestedCriteria());
        }

        public Builder addAll(Collection<ResourceQueryCriteria> filters) {
            this.filters.addAll(filters);
            return me();
        }

        public OptimizedResourceQueryCriteria build() {
            Table<String, Short, List<ResourcePathFilter>> table = HashBasedTable.create();

            List<ResourcePathFilter> nonIndexCriteria = new ArrayList<>();
            for (ResourceQueryCriteria criteria : filters) {
                if (criteria instanceof ResourcePathFilter) {
                    ResourcePathFilter<?> f = (ResourcePathFilter) criteria;

                    MappedField<?> mf = f.getPath().lastMappedField();

                    if (mf.isIndexed()) {
                        for (MappedIndexField indexedField : mf.getIndexes()) {
                            List<ResourcePathFilter> values = table
                                .get(indexedField.getIndexName(), indexedField.getPosition());
                            if (values == null) {
                                values = new ArrayList<>();
                                table.put(indexedField.getIndexName(), indexedField.getPosition(),
                                    values);
                            }
                            values.add(f);
                        }
                    } else {
                        nonIndexCriteria.add(f);
                    }
                }
            }

            String index = identifyBestIndex(table);
            List<ResourcePathFilter> indexCriteria = new ArrayList<>();
            for (String idx : table.rowKeySet()) {
                Map<Short, List<ResourcePathFilter>> row = table.row(idx);
                List<ResourcePathFilter> target = nonIndexCriteria;
                if (Objects.equals(idx, index)) {
                    target = indexCriteria;
                    //TODO we have to validate and collapse multiple filters on an index
                    // only in or between would be supported for dynamo
                    // https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/Expressions.OperatorsAndFunctions.html
                }
                for (Collection<ResourcePathFilter> values : row.values()) {
                    target.addAll(values);
                }
            }

            return new BasicOptimizedResourceQueryCriteria(indexCriteria, nonIndexCriteria);
        }

        private String identifyBestIndex(Table<String, Short, List<ResourcePathFilter>> table) {
            int max = 0;
            String result = null;
            //TODO there is probably a better algorithm and need for index hints
            for (String idx : table.rowKeySet()) {
                short i = 0;
                Map<Short, List<ResourcePathFilter>> map = table.row(idx);
                if (!map.containsKey(i)) {
                    // does not contain criteria for first position of index, ignore.
                    continue;
                }
                for (i = 1; i < 10; i++) {
                    if (!map.containsKey(i)) {
                        // no additional index criteria specified.
                        break;
                    }
                }
                if (i >= max) {
                    result = idx;
                }
            }
            return result;
        }
    }

}
