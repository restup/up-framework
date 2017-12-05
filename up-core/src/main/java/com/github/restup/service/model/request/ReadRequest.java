package com.github.restup.service.model.request;

import com.github.restup.service.model.Identified;
import java.io.Serializable;

/**
 * Request interface for read operations
 *
 * @author abuttaro
 */
public interface ReadRequest<T, ID extends Serializable> extends ResourceRequest<T>, Identified<ID> {

}
