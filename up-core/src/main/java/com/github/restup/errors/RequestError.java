package com.github.restup.errors;

/**
 * A request error, providing necessary details for JSON API errors.
 *
 * @author andy.buttaro
 */
public interface RequestError {

    String getId();

    String getCode();

    String getTitle();

    String getDetail();

    ErrorSource getSource();

    Object getMeta();

    String getStatus();

    int getHttpStatus();

}
