package com.github.restup.controller;

import com.github.restup.controller.model.ResourceControllerResponse;
import com.github.restup.errors.DebugRequestError;
import com.github.restup.errors.ErrorObjectException;
import com.github.restup.errors.RequestError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultExceptionHandler implements ExceptionHandler {

    private final static Logger log = LoggerFactory.getLogger(DefaultExceptionHandler.class);

    public Object handleException(ResourceControllerResponse response, Throwable e) {
        ErrorObjectException result;
        if (e instanceof ErrorObjectException) {
            result = (ErrorObjectException) e;
            if (log.isDebugEnabled()) {
                for (RequestError err : result.getErrors()) {
                    if (err instanceof DebugRequestError) {
                        ((DebugRequestError) err).logStackTrace();
                    }
                }
            } else if (result.getHttpStatus() == 500) {
                log.error("An unexpected error occurred. ", e.getCause());
            }
        } else {
            log.error("An unexpected error occurred", e);
            result = new ErrorObjectException(e);
        }
        response.setStatus(result.getHttpStatus());
        return result;
    }

}
