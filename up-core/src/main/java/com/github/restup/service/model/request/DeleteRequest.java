package com.github.restup.service.model.request;

import com.github.restup.service.model.Identified;
import java.io.Serializable;

/**
 * Request interface for delete operations
 *
 * @author abuttaro
 */
public interface DeleteRequest<T, ID extends Serializable> extends ResourceRequest<T>, Identified<ID> {

}
