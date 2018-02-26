package com.github.restup.mapping;

import java.util.Comparator;
import org.apache.commons.lang3.StringUtils;
import com.github.restup.mapping.fields.MappedField;

class DefaultMappedFieldComparator implements Comparator<MappedField<?>> {

	@Override
    public int compare(MappedField<?> a, MappedField<?> b) {
		if (a == null) {
            return b == null ? 0 : 1;
		}
        if (b == null) {
            return -1;
        }
		if (a.isIdentifier()) {
            if (!b.isIdentifier()) {
                return -1;
            }
        } else if (b.isIdentifier()) {
			return 1;
		}
        int result = StringUtils.compare(a.getApiName(), b.getApiName());
        if (result == 0) {
            result = StringUtils.compare(a.getBeanName(), b.getBeanName());
		}
        return result;
	}

}