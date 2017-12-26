package com.github.restup.errors;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static com.github.restup.util.UpUtils.unmodifiableList;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;

/**
 * An exception containing {@link RequestError}s
 */
public class ErrorObjectException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private final List<RequestError> errors;

	/**
	 * Sorts errors by httpStatus, cause
	 */
	public ErrorObjectException(List<RequestError> errors) {
		super(isEmpty(errors) ? null : errors.iterator().next().getDetail(), cause(errors));
		Collections.sort(errors, new RequestErrorComparator());
		this.errors = unmodifiableList(errors);
	}

	/**
	 * Converts array to {@link List}, calls {@link #ErrorObjectException(List)}
	 */
	public ErrorObjectException(RequestError... errors) {
		this(Arrays.asList(errors));
	}

	/**
	 * builds a new {@link RequestError}, calls
	 * {@link #ErrorObjectException(RequestError...)}
	 */
	public ErrorObjectException(Throwable t) {
		this(ErrorBuilder.error(null, t).build());
	}

	/**
	 * @return the first non null cause from the list of errors, null if none
	 */
	private static Throwable cause(List<RequestError> errors) {
		if (errors != null) {
			for (RequestError error : errors) {
				if (error instanceof DefaultRequestError) {
					DefaultRequestError e = (DefaultRequestError) error;
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
		return error != null ? error.getHttpStatus() : ErrorBuilder.ErrorCodeStatus.INTERNAL_SERVER_ERROR.getHttpStatus();
	}

	public String getCode() {
		RequestError error = getPrimaryError();
		return error != null ? error.getCode() : ErrorBuilder.ErrorCodeStatus.INTERNAL_SERVER_ERROR.name();
	}

	private RequestError getPrimaryError() {
		Optional<RequestError> error = errors.stream().findFirst();
		return error.isPresent() ? error.get() : null;
	}

	/**
	 * Sorts errors by http status desc, then naturally ordered by source
	 */
	private final class RequestErrorComparator implements Comparator<RequestError> {

		public int compare(RequestError a, RequestError b) {
			if (a.getStatus() != b.getStatus()) {
				return a.getHttpStatus() > b.getHttpStatus() ? -1 : 1;
			}
			String sourceA = getSource(a.getSource());
			String sourceB = getSource(b.getSource());
			if (sourceA == null) {
				return sourceB == null ? 0 : 1;
			} else if (sourceB == null) {
				return -1;
			}
			return sourceA.compareTo(sourceB);
		}

		private String getSource(ErrorSource err) {
			return err == null ? null : err.getSource();
		}
	}
}
