package com.github.restup.path;

import com.github.restup.errors.ErrorBuilder;
import com.github.restup.errors.ErrorSource;
import com.github.restup.errors.Errors;
import com.github.restup.mapping.MappedClass;
import com.github.restup.mapping.fields.IterableField;
import com.github.restup.mapping.fields.MappedField;
import com.github.restup.mapping.fields.ReadableField;
import com.github.restup.mapping.fields.WritableField;
import com.github.restup.path.ResourcePath.Builder.Mode;
import com.github.restup.registry.Resource;
import com.github.restup.registry.ResourceRegistry;
import com.github.restup.service.model.ResourceData;
import com.github.restup.util.Assert;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * Provides support for accessing and representing fields of an object or object
 * graph.
 */
public class ResourcePath implements ErrorSource {

	// TODO doc

	public final static PathValue ID = new ConstantPathValue("id");
	public final static PathValue TYPE = new ConstantPathValue("type");
	public final static PathValue DATA = new DataPathValue();
	public final static PathValue ATTRIBUTES = new ConstantPathValue("attributes");
	private final ResourcePath prior;
	private final PathValue value;
	private ResourcePath next;
	private Resource<?, ?> resource;
	private boolean valid;

	ResourcePath(ResourcePath prior, PathValue value) {
		super();
		this.prior = prior;
		Assert.notNull(value, "value must not be null");
		this.value = value;
		if (prior != null) {
			prior.setNext(this);
		}
	}

	public static String getRelationshipResource(ResourceRegistry registry, ResourcePath path) {
		MappedField<?> mf = path.lastMappedField();
		return mf == null ? null : mf.getRelationshipResource(registry);
	}

	public static MappedField<?> findApiField(MappedClass<?> mappedClass, String field) {
		return findField(Mode.API, mappedClass, field);
	}

	public static MappedField<?> findPersistedField(MappedClass<?> mappedClass, String field) {
		return findField(Mode.PERSISTED, mappedClass, field);
	}

	public static MappedField<?> findBeanField(MappedClass<?> mappedClass, String field) {
		return findField(Mode.BEAN, mappedClass, field);
	}

	private static MappedField<?> findField(Mode mode, MappedClass<?> mappedClass, String field) {
		for (MappedField<?> mappedField : mappedClass.getAttributes()) {
			if (matchesField(mode, mappedField, field)) {
				return mappedField;
			}
		}
		return null;
	}

	private static boolean matchesField(Mode mode, MappedField<?> mappedField, String field) {
		switch (mode) {
		case BEAN:
			return Objects.equals(field, mappedField.getBeanName());
		case API:
			return Objects.equals(field, mappedField.getApiName());
		case PERSISTED:
			return Objects.equals(field, mappedField.getApiName());
		}
		return false;
	}

	/**
	 * @param paths
	 * @param other
	 * @return
	 */
	public static boolean hasPath(List<ResourcePath> paths, ResourcePath other) {
		if (paths == null || other == null) {
			return false;
		}
		ResourcePath path = other.firstMappedFieldPath();
		for (ResourcePath b : paths) {
			if (path.startsWith(b.firstMappedFieldPath())) {
				return true;
			}
		}
		return false;
	}

	public final static Builder data(Resource<?, ?> resource) {
		return new Builder(resource).data();
	}

	public final static Builder builder(ResourceRegistry registry) {
		return new Builder(registry);
	}

	public final static Builder builder(ResourcePath path) {
		return new Builder(path);
	}

	public final static Builder builder(ResourceRegistry registry, Class<?> resourceClass) {
		return new Builder(registry, resourceClass);
	}

	public final static Builder builder(Resource<?, ?> resource) {
		return new Builder(resource);
	}

	public final static ResourcePath path(ResourcePath path, MappedField<?> mf) {
		return new Builder(path).append(mf).build();
	}

	public final static ResourcePath path(ResourceRegistry registry, Class<?> resourceClass, String path) {
		return builder(registry, resourceClass).path(path).build();
	}

	public final static ResourcePath path(Resource<?, ?> resource, String path) {
		return builder(resource).path(path).build();
	}

	public final static ResourcePath apiPath(Resource<?, ?> resource, int index, String path) {
		return builder(resource).setMode(Mode.API).data(index).path(path).build();
	}

	public final static ResourcePath apiPath(Resource<?, ?> resource, String path) {
		return builder(resource).setMode(Mode.API).path(path).build();
	}

	public final static ResourcePath path(Resource<?, ?> resource, MappedField<?> mappedField) {
		return builder(resource).append(mappedField).build();
	}

	public final static ResourcePath path(Resource<?, ?> resource, int index, MappedField<?> mappedField) {
		return builder(resource).data(index).append(mappedField).build();
	}

	public static List<ResourcePath> paths(Resource<?, ?> resource, String... beanPaths) {
		List<ResourcePath> paths = new ArrayList<ResourcePath>();
		for (String beanPath : beanPaths) {
			paths.add(path(resource, beanPath));
		}
		return paths;
	}

	public static ResourcePath idPath(Resource<?, ?> resource) {
		return path(resource, resource.getIdentityField());
	}

	public ResourcePath last() {
		ResourcePath current = this;
		while (current.next != null) {
			current = current.next;
		}
		return current;
	}

	public ResourcePath first() {
		ResourcePath current = this;
		while (current.prior != null) {
			current = current.prior;
		}
		return current;
	}

	public PathValue firstValue() {
		PathValue result = null;
		ResourcePath first = this;
		while (first != null) {
			result = first.value;
			first = first.prior;
		}
		return result;
	}

	public PathValue lastValue() {
		PathValue result = null;
		ResourcePath last = this;
		while (last != null) {
			result = last.value;
			last = last.next;
		}
		return result;
	}

	public ResourcePath firstMappedFieldPath() {
		return first(MappedFieldPathValue.class);
	}

	public MappedFieldPathValue<?> firstMappedField() {
		return firstValue(MappedFieldPathValue.class);
	}

	public MappedFieldPathValue<?> lastMappedFieldPathValue() {
		return lastValue(MappedFieldPathValue.class);
	}

	public MappedField<?> lastMappedField() {
		MappedFieldPathValue<?> pv = lastMappedFieldPathValue();
		return pv == null ? null : pv.getMappedField();
	}

	public ResourcePath first(Class<? extends PathValue> type) {
		ResourcePath current = first();
		while (current != null) {
			if (current.value != null && type.isAssignableFrom(current.value.getClass())) {
				return current;
			}
			current = current.next;
		}
		return null;
	}

	public ResourcePath last(Class<? extends PathValue> type) {
		ResourcePath current = last();
		while (current != null) {
			if (current.value != null && type.isAssignableFrom(current.value.getClass())) {
				return current;
			}
			current = current.prior;
		}
		return current;
	}

	public ResourcePath skip(Class<? extends PathValue> type) {
		ResourcePath current = this;
		while (current != null && type.isAssignableFrom(current.value.getClass())) {
			current = current.next;
		}
		return current;
	}

	@SuppressWarnings("unchecked")
	public <P extends PathValue> P lastValue(Class<P> type) {
		ResourcePath path = last(type);
		return path == null ? null : (P) path.value;
	}

	@SuppressWarnings("unchecked")
	public <P extends PathValue> P firstValue(Class<P> type) {
		ResourcePath path = first(type);
		return path == null ? null : (P) path.value;
	}

	public boolean hasErrors() {
		return last(InvalidPathValue.class) != null;
	}

	public ResourcePath prior() {
		return prior;
	}

	public boolean hasNext() {
		return next != null;
	}

	public ResourcePath next() {
		return next;
	}

	public PathValue value() {
		return value;
	}

	public boolean isValid() {
		return valid;
	}

	private void setValid(boolean valid) {
		this.valid = valid;
	}

	public Resource<?, ?> getResource() {
		return first().resource;
	}

	private void setResource(Resource<?, ?> resource) {
		this.resource = resource;
	}

	private void setNext(ResourcePath next) {
		this.next = next;
	}

	public Object getValue(Object instance) {
		Object result = instance;
		ResourcePath current = first();
		// XXX cheating here using ResourceData... should see if object has a data
		// property
		if (!(instance instanceof ResourceData)) {
			current = current.skip(DataPathValue.class);
		}
		while (current != null) {
			if (current.supportsType(result)) {
				result = current.readValue(result);
				if (result == null) {
					return result;
				}
			} else if (instance instanceof Iterable) {
				return collectValues(new ArrayList<Object>(), instance);
			}
			current = current.next();
		}
		// if result is still instance, nothing was read
		return result == instance ? null : result;
	}

	@SuppressWarnings("rawtypes")
	public Collection<Object> collectValues(Collection<Object> into, Object instance) {
		if (instance instanceof Collection) {
			for (Object o : (Collection) instance) {
				addAll(into, getValue(o));
			}
		} else {
			addAll(into, getValue(instance));
		}
		return into;
	}

	@SuppressWarnings("rawtypes")
	private void addAll(Collection<Object> into, Object value) {
		if (value instanceof Iterable) {
			for (Object o : (Iterable) value) {
				into.add(o);
			}
		} else {
			into.add(value);
		}
	}

	public void setValue(Object instance, Object value) {
		if (isValid()) {
			Object currentInstance = instance;
			ResourcePath current = first();
			while (current != null) {
				if (current.supportsType(currentInstance)) {
					if (current.hasNext()) {
						currentInstance = current.populatePath(current, currentInstance);
					} else {
						current.writeValue(currentInstance, value);
					}
				}
				current = current.next();
			}
		}
	}

	@SuppressWarnings("rawtypes")
	private Object populatePath(ResourcePath path, Object instance) {
		if (path != null) {
			Object o = readValue(instance);
			if (o == null && this.value instanceof WritableField) {
				WritableField writable = (WritableField) value;
				o = writable.createInstance();
				writeValue(instance, o);
			}
			return o;
		}
		return instance;
	}

	private boolean supportsType(Object instance) {
		return (instance != null && value.supportsType(instance.getClass()));
	}

	private Object readValue(Object instance) {
		if (supportsType(instance)) {
			ReadableField<?> readable = (ReadableField<?>) value;
			return readable.readValue(instance);
		}
		return null;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void writeValue(Object instance, Object val) {
		if (value instanceof WritableField) {
			WritableField writable = (WritableField) value;
			writable.writeValue(instance, val);
		}
	}

	@Override
	public int hashCode() {
		return Objects.hash(next, value);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		ResourcePath other = (ResourcePath) obj;
		if (!equalsValue(other)) {
			return false;
		}
		return first().equalsPath(other.first());
	}

	private boolean equalsValue(ResourcePath other) {
		if (value == null) {
			if (other.value != null) {
				return false;
			}
		}
		return value.equals(other.value);
	}

	/**
	 * @return true if this path subsequent paths is the same as other path and
	 *         subsequent values. Does not consider prior.
	 */
	public boolean equalsPath(ResourcePath other) {
		if (other == null) {
			return false;
		}
		if (!equalsValue(other)) {
			return false;
		}
		if (next != null) {
			return next.equalsPath(other.next);
		}
		if (other.next != null) {
			return false;
		}
		return true;
	}

	public String getSource() {
		return first().join(Mode.API, "/", true);
	}

	public String getPersistedPath() {
		return firstMappedFieldPath().join(Mode.PERSISTED, ".");
	}

	public String getBeanPath() {
		return firstMappedFieldPath().join(Mode.BEAN, ".");
	}

	public String getApiPath() {
		return firstMappedFieldPath().join(Mode.API, ".");
	}

	private String join(Mode mode, String separator) {
		return join(mode, separator, false);
	}

	private String join(Mode mode, String separator, boolean startsWithSeparator) {
		StringBuilder sb = new StringBuilder();
		if (startsWithSeparator) {
			sb.append(separator);
		}
		ResourcePath current = this;
		while (current != null) {
			sb.append(current.getPath(mode));
			current = current.next;
			if (current != null) {
				sb.append(separator);
			}
		}
		return sb.toString();
	}

	private String getPath(Mode mode) {
		switch (mode) {
		case PERSISTED:
			return value.getPersistedPath();
		case API:
			return value.getApiPath();
		case BEAN:
			return value.getBeanPath();
		}
		return null;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		ResourcePath path = first();
		while (path != null) {
			sb.append(path.value);
			path = path.next;
			if (path != null) {
				sb.append(".");
			}
		}
		return sb.toString();
	}

	public boolean startsWith(ResourcePath other) {
		return firstMappedFieldPath().isSubPathOf(other.firstMappedFieldPath());
	}

	//
	// public ResourcePath copy() {
	// ResourcePath copy = null;
	// Builder b = builder();
	// ResourcePath path = first();
	// while ( path != null ) {
	// b.append(path.value());
	// if ( path.value == this.value) {
	// copy = b.current;
	// }
	// path = path.next;
	// }
	// b.build();
	// return copy;
	// }

	/**
	 * @return true if this path matches or is a sub path of other. ex "foo.bar" is
	 *         a sub path of "foo"
	 */
	public boolean isSubPathOf(ResourcePath other) {
		if (other == null) {
			return false;
		}
		if (!equalsValue(other)) {
			// paths don't match
			return false;
		}
		if (!other.hasNext()) {
			// was a match until this point
			return true;
		}
		if (!hasNext()) {
			// other has additional paths but this does not
			return false;
		}
		return next().isSubPathOf(other.next());
	}

	public ResourcePath append(MappedField<?> mappedField) {
		return ResourcePath.builder(this).append(mappedField).build();
	}

	public ResourcePath append(int index) {
		return ResourcePath.builder(this).index(index).build();
	}

	public final static class Builder {

		private final static Function<PathValue, Boolean> NOT_NULL = new Function<PathValue, Boolean>() {
			public Boolean apply(PathValue t) {
				return t != null;
			}
		};
		private final static Function<PathValue, Boolean> MAPPED = new Function<PathValue, Boolean>() {
			public Boolean apply(PathValue t) {
				return t instanceof MappedFieldPathValue;
			}
		};
		private final ResourceRegistry registry;
		private ResourcePath root = null;
		private ResourcePath prior = null;
		private ResourcePath current = null;
		private MappedClass<?> currentMappedClass;
		private boolean currentCollection;
		private Resource<?, ?> resource;

		private Mode mode = Mode.BEAN;
		private boolean invalid;
		private Errors errors;
		private boolean quiet;
		private Function<PathValue, Boolean> filter;

		public Builder(MappedClass<?> mappedClass) {
			this(ResourceRegistry.getInstance(), mappedClass);
		}

		public Builder(ResourceRegistry registry, MappedClass<?> mappedClass) {
			this(registry);
			setMappedClass(mappedClass);
		}

		public Builder(Resource<?, ?> resource) {
			this(resource.getRegistry(), resource.getMapping());
			this.resource = resource;
		}

		public Builder(ResourceRegistry registry, Class<?> mappedClass) {
			this(registry);
			setMappedClass(registry.getMappedClass(mappedClass));
		}

		public Builder(ResourcePath parent) {
			this(parent.getResource());
			append(parent);
		}

		private Builder(ResourceRegistry registry) {
			Assert.notNull(registry, "registry may not be null");
			this.registry = registry;
			filter = NOT_NULL;
		}

		private Builder me() {
			return this;
		}

		public Builder setMode(Mode mode) {
			this.mode = mode;
			return me();
		}

		public void setMappedClass(MappedClass<?> mappedClass) {
			currentMappedClass = mappedClass;
			if (resource == null && mappedClass != null) {
				resource = registry.getResource(mappedClass.getName());
			}
		}

		public Builder data() {
			if (root != null) {
				throw new IllegalStateException("data not allowed here");
			}
			return append(DATA);
		}

		public Builder data(int index) {
			return append(DATA).index(index);
		}

		public Builder index(int index) {
			if (index < 0) {
				throw new IllegalArgumentException("Invalid index " + index);
			}
			if (!supportsIndex()) {
				throw new IllegalStateException("Index not allowed here");
			}
			return append(new IndexPathValue(index));
		}

		@SuppressWarnings("rawtypes")
		private boolean supportsIndex() {
			if (current != null) {
				if (currentCollection || current.value() == DATA) {
					return true;
				}
				if (current.value() instanceof MappedFieldPathValue) {
					MappedFieldPathValue pv = (MappedFieldPathValue) current.value();
					MappedField field = pv.getMappedField();
					if ( field.isCollection() ) {
						return true;
					}
				}
			}
			// TODO collections, arrays
			return false;
		}

		public Builder path(String path) {
			if (path != null) {
				String[] paths = path.split("\\.");
				for (String s : paths) {
					append(s);
				}
			}
			return me();
		}

		public Builder append(String field) {
			if (invalid) {
				return append(new InvalidPathValue(field));
			} else {
				if (currentCollection) {
					try {
						return index(Integer.valueOf(field));
					} catch (NumberFormatException e) {
						// ignore and assume field is a path if not a valid index
					}
				}

				if (currentMappedClass == null) {
					throw new IllegalStateException("mappedClass is not set");
				}
				MappedField<?> mappedField = findField(field);
				if (mappedField == null) {
					return append(new InvalidPathValue(field));
				} else {
					return append(mappedField);
				}
			}
		}

		public <T> Builder append(MappedField<T> mappedField) {
			if (mappedField == null) {
				return me();
			}
			setMappedField(mappedField);
			return append(new MappedFieldPathValue<T>(mappedField));
		}

		@SuppressWarnings("rawtypes")
		private void setMappedField(MappedField mappedField) {
			Type type = mappedField.getType();
			if (mappedField instanceof IterableField) {
				IterableField iterableField = (IterableField) mappedField;
				type = iterableField.getGenericType();
				currentCollection = true;
			}
			setMappedClass(registry.getMappedClass(type));
		}

		private MappedField<?> findField(String field) {
			return ResourcePath.findField(mode, currentMappedClass, field);
		}

		public Builder append(PathValue... pathValues) {
			for (PathValue value : pathValues) {
				append(value);
			}
			return me();
		}

		public Builder append(ResourcePath path) {
			if (path != null) {
				ResourcePath current = path;
				while (current != null) {
					if (resource == null) {
						resource = current.getResource();
						currentMappedClass = resource.getMapping();
					}
					append(current.value());
					current = current.next();
				}
			}
			return me();
		}

		@SuppressWarnings("rawtypes")
		public Builder append(PathValue pv) {
			if (filter.apply(pv)) {
				prior = current;
				current = new ResourcePath(prior, pv);
				if (root == null) {
					root = current;
				}
				if (pv instanceof MappedFieldPathValue) {
					setMappedField(((MappedFieldPathValue) pv).getMappedField());
				} else if (pv instanceof InvalidPathValue) {
					invalid = true;
				} else if (pv instanceof IndexPathValue) {
					currentCollection = false;
				}
				current.setValid(!invalid);
			}
			return me();
		}

		/**
		 * If true errors will be ignored
		 */
		public Builder setQuiet(boolean quiet) {
			this.quiet = quiet;
			return me();
		}

		public Builder setErrors(Errors errors) {
			this.errors = errors;
			return me();
		}

		public Builder filterMappedPathValues() {
			return setPathValueFilter(MAPPED);
		}

		public Builder setPathValueFilter(Function<PathValue, Boolean> filter) {
			this.filter = filter;
			return me();
		}

		public boolean isInvalid() {
			return invalid;
		}

		public ResourcePath build() {
			ResourcePath result = root;
			result.setValid(!invalid);
			result.setResource(resource);
			if (invalid) {
				ErrorBuilder error = ErrorBuilder.builder().code("INVALID_PATH").title("Invalid Path")
						.detail("The specified path is not valid.").path(result);
				if (errors != null) {
					errors.addError(error);
				} else if (!quiet) {
					error.throwError();
				}
			}
			root = null;
			prior = null;
			current = null;
			return result;
		}

		public static enum Mode {
			API, BEAN, PERSISTED;
		}

	}

}
