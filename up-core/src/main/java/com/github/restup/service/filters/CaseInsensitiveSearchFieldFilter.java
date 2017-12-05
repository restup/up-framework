package com.github.restup.service.filters;

import com.github.restup.annotations.filter.PreCreateFilter;
import com.github.restup.annotations.filter.PreUpdateFilter;
import com.github.restup.mapping.fields.MappedField;
import com.github.restup.path.MappedFieldPathValue;
import com.github.restup.path.ResourcePath;
import com.github.restup.query.ResourceQueryDefaults;
import com.github.restup.registry.Resource;
import com.github.restup.service.model.request.CreateRequest;
import com.github.restup.service.model.request.UpdateRequest;
import java.util.List;

public class CaseInsensitiveSearchFieldFilter {

    @PreCreateFilter
    public void filter(CreateRequest<?> request) {
        applyCaseInsensitiveFields(request.getData(), null, Resource.getAllPaths(request.getResource()));
    }

    @PreUpdateFilter
    public void filter(UpdateRequest<?, ?> request, ResourceQueryDefaults defaults) {
        if (request.getRequestedPaths() != null) {
            Object data = request.getData();
            applyCaseInsensitiveFields(data, defaults, request.getRequestedPaths());
        }
    }

    private void applyCaseInsensitiveFields(Object data, ResourceQueryDefaults defaults, List<ResourcePath> paths) {
        for (ResourcePath path : paths) {
            MappedFieldPathValue<?> mfpv = path.lastMappedFieldPathValue();
            ResourcePath insensitivePath = pathToCaseInsensitive(path, mfpv);
            if (insensitivePath != null) {
                if (defaults != null) {
                    defaults.addRequired(insensitivePath);
                }
                Object mixedCase = path.getValue(data);
                Object caseInsensitive = MappedField.toCaseInsensitive(mfpv, mixedCase);
                insensitivePath.setValue(data, caseInsensitive);
            }
        }
    }

    private ResourcePath pathToCaseInsensitive(ResourcePath path, MappedFieldPathValue<?> mfpv) {
        if (MappedField.isCaseInsensitive(mfpv.getMappedField())) {
            ResourcePath current = path.first();
            ResourcePath.Builder b = ResourcePath.builder(current.getResource());
            while (current != null) {
                if (current.value() == mfpv) {
                    b.append(mfpv.getMappedField().getCaseInsensitiveSearchField());
                    return b.build();
                }
                b.append(current.value());
                current = current.next();
            }
        }
        return null;
    }

}
