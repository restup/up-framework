package com.github.restup.spring.boot.autoconfigure.factory;

import java.util.Comparator;
import com.github.restup.mapping.MappedClass;
import com.github.restup.mapping.fields.MappedField;

public interface MappedFieldComparatorFactory {

    default Comparator<MappedField<?>> getMappedFieldComparator() {
        return MappedClass.getDefaultFieldComparator();
    }

}
