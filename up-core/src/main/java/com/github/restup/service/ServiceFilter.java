package com.github.restup.service;

import java.io.Serializable;
import com.github.restup.registry.Resource;

/**
 * Service filters do not have to implement any interfaces, but when using a filter which is used as a default across resources, it may be necessary to implement ServiceFilter to identify whether the default resource may apply to different types of resources. <p> For example, many resources may not be physically deleted and instead would have a status field indicating their delete state.  It is useful to create a filter registered as a default in {@link RegistrySettings#getDefaultServiceFilters()} that can be applied to all resources implementing a common status filter, and be ignored by resources that do not implement such an interface.
 *
 * @author abuttaro
 */
public interface ServiceFilter {

    <T, ID extends Serializable> boolean accepts(Resource<T, ID> resource);

}
