package com.github.restup.errors;

import static com.github.restup.util.UpUtils.unmodifiableList;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import com.github.restup.registry.Resource;

/**
 * An exception containing {@link RequestError}s
 */
public class RequestErrorException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private final List<RequestError> errors;

	/**
     * Sorts errors by httpStatus, cause
     * 
     * @param errors causing execption
     */
	public RequestErrorException(List<RequestError> errors) {
		super(isEmpty(errors) ? null : errors.iterator().next().getDetail(), cause(errors));
		Collections.sort(errors, new RequestErrorComparator());
		this.errors = unmodifiableList(errors);
	}

	/**
     * Converts array to {@link List}, calls {@link #RequestErrorException(List)}
     * 
     * @param errors causing exception
     */
	public RequestErrorException(RequestError... errors) {
		this(Arrays.asList(errors));
	}

	/**
     * builds a new {@link RequestError}, calls {@link #RequestErrorException(RequestError...)}
     * 
     * @param t cause of {@link RequestError}
     */
	public RequestErrorException(Throwable t) {
		this(RequestError.error(null, t).build());
	}

    public static RequestErrorException of(Throwable t) {
        return new RequestErrorException(RequestError.of(t));
    }

    public static RequestErrorException of(Resource<?, ?> resource, Throwable t) {
        return new RequestErrorException(RequestError.of(resource, t));
    }

    public static void rethrow(Throwable t) {
        throw of(t);
    }

	/**
	 * @return the first non null cause from the list of errors, null if none
	 */
	private static Throwable cause(List<RequestError> errors) {
		if (errors != null) {
			for (RequestError error : errors) {
				if (error instanceof BasicRequestError) {
					BasicRequestError e = (BasicRequestError) error;
					if (e.getCause() != null) {
						return e.getCause();
					}
				}
			}
		}
		return null;
	}

	public List<RequestError> getErrors() {
		return errors;
	}

	/**
	 * @return the greatest httpStatus from all errors in this exception, or 500 if
	 *         none.
	 */
	public int getHttpStatus() {
		RequestError error = getPrimaryError();
		return error != null ? error.getHttpStatus() : StatusCode.INTERNAL_SERVER_ERROR.getHttpStatus();
	}

	public String getCode() {
		RequestError error = getPrimaryError();
		return error != null ? error.getCode() : StatusCode.INTERNAL_SERVER_ERROR.name();
	}

	public RequestError getPrimaryError() {
		Optional<RequestError> error = errors.stream().findFirst();
		return error.isPresent() ? error.get() : null;
	}

	/**
	 * Sorts errors by http status desc, then naturally ordered by source
	 */
	private final class RequestErrorComparator implements Comparator<RequestError> {

		@Override
        public int compare(RequestError a, RequestError b) {
		    int result = StringUtils.compare(a.getStatus(), b.getStatus());
			if ( result == 0) {
	            String sourceA = getSource(a.getSource());
	            String sourceB = getSource(b.getSource());
	            result = StringUtils.compare(sourceA, sourceB);
			} else {
			    // want descending status
			    result *= -1;
			}
			return result;
		}

		private String getSource(ErrorSource err) {
			return err == null ? null : err.getSource();
		}
	}

}
