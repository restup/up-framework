package com.github.restup.service.model;

/**
 * Promotes consistency for request/response signatures
 *
 * @param <T>
 * @author abuttaro
 */
public interface ResourceData<T> {

    T getData();

}
