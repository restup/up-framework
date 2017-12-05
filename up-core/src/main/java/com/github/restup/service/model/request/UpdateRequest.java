package com.github.restup.service.model.request;

import com.github.restup.service.model.Identified;
import java.io.Serializable;

/**
 * Request interface for update operations.
 *
 * @author abuttaro
 */
public interface UpdateRequest<T, ID extends Serializable> extends PersistenceRequest<T>, Identified<ID> {

}
