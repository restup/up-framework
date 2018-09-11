package com.github.restup.service.model.response;

import com.github.restup.service.model.ResourceData;
import java.util.List;

public interface ResourceResult<T> extends ResourceData<T> {

    /**
     * @return A list of {@link RelatedResourceResult}s requested and related to {@link #getData()}
     * resource(s)
     */
    List<RelatedResourceResult<?, ?>> getRelatedResourceResults();

}
