package com.github.restup.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.github.restup.controller.model.MediaType;
import com.github.restup.controller.model.ResourceControllerRequest;
import com.github.restup.controller.model.ResourceControllerResponse;
import com.github.restup.errors.DebugRequestError;
import com.github.restup.errors.RequestError;
import com.github.restup.errors.RequestErrorException;

/**
 * Default {@link ExceptionHandler} which ensures all Exceptions are wrapped
 * with an {@link RequestErrorException} for detailed error responses.
 * 
 * @author abuttaro
 *
 */
class DefaultExceptionHandler implements ExceptionHandler {

	private final static Logger log = LoggerFactory.getLogger(DefaultExceptionHandler.class);

    private static volatile DefaultExceptionHandler instance = null;

    private DefaultExceptionHandler() {
        super();
    }

    public static DefaultExceptionHandler getInstance() {
        if (instance == null) {
            synchronized (DefaultExceptionHandler.class) {
                if (instance == null) {
                    instance = new DefaultExceptionHandler();
                }
            }
        }
        return instance;
    }

	@Override
    public Object handleException(ResourceControllerRequest request, ResourceControllerResponse response, Throwable e) {
		RequestErrorException result;
		if (e instanceof RequestErrorException) {
			result = (RequestErrorException) e;
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
			result = new RequestErrorException(e);
		}

        response.setHeader("Content-Type", request != null
                ? request.getContentType()
                : MediaType.APPLICATION_JSON.getContentType());
        
		response.setStatus(result.getHttpStatus());
		return result;
	}

}
