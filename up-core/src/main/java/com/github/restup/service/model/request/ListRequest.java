package com.github.restup.service.model.request;

import java.util.List;

/**
 * Request interface for list operations
 *
 * @author abuttaro
 */
public interface ListRequest<T> extends ResourceRequest<List<T>>, QueryRequest {

}
