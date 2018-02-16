package com.github.restup.service.filters;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import javax.validation.metadata.ConstraintDescriptor;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.github.restup.annotations.filter.PreCreateFilter;
import com.github.restup.annotations.filter.PreUpdateFilter;
import com.github.restup.errors.Errors;
import com.github.restup.errors.RequestError;
import com.github.restup.mapping.MappedClass;
import com.github.restup.mapping.fields.IterableField;
import com.github.restup.mapping.fields.MappedField;
import com.github.restup.mapping.fields.ReadableField;
import com.github.restup.path.IndexPathValue;
import com.github.restup.path.MappedFieldPathValue;
import com.github.restup.path.ResourcePath;
import com.github.restup.registry.Resource;
import com.github.restup.registry.ResourceRegistry;
import com.github.restup.service.ServiceFilter;
import com.github.restup.service.model.request.CreateRequest;
import com.github.restup.service.model.request.UpdateRequest;

public class JavaxValidationFilter implements ServiceFilter {

    private final Logger log = LoggerFactory.getLogger(JavaxValidationFilter.class);

    private final Validator validator;

    public JavaxValidationFilter(Validator validator) {
        super();
        this.validator = validator;
    }

    @SuppressWarnings("rawtypes")
    protected static Object length(Object value) {
        if (value instanceof String) {
            return ((String) value).trim().length();
        }
        if (value instanceof Collection) {
            return ((Collection) value).size();
        }
        return 0;
    }

    @PreCreateFilter
    public <T, ID extends Serializable> void validateCreate(ResourceRegistry registry, Errors errors, CreateRequest<T> request, Resource<T, ID> resource) {
        //XXX this does not have batch info with item for correct error path
        T target = request.getData();
        MappedClass<T> mapping = resource.getMapping();
        validateObject(registry, errors, ResourcePath.builder(resource).data().build(), mapping, target);
    }

    /**
     * Applies javax validations to only fields specified in request
     * 
     * @param <T> resource type
     * @param <ID> resource id type
     * @param registry instance
     * @param errors to collect errors found
     * @param request object
     * @param resource object
     */
    @PreUpdateFilter
    public <T, ID extends Serializable> void validateUpdate(ResourceRegistry registry, Errors errors, UpdateRequest<T, ID> request, Resource<T, ID> resource) {
        validatePaths(registry, errors, request.getData(), request.getRequestedPaths());
    }

    private <T, ID extends Serializable> void validatePaths(ResourceRegistry registry, Errors errors, T target, List<ResourcePath> paths) {
        if (paths != null) {
            for (ResourcePath path : paths) {
                validatePath(registry, errors, path, target);
            }
        }
    }

    private void validatePath(ResourceRegistry registry, Errors errors, ResourcePath path, Object target) {
        MappedFieldPathValue<?> mfpv = path.lastMappedFieldPathValue();
        if (mfpv != null) {
            Object validationTarget = target;
            ResourcePath current = path.firstMappedFieldPath();
            while (current.value() != null) {
                if (current.value() == mfpv) {
                    validatePath(registry, errors, path, validationTarget, mfpv.getMappedField());
                    return;
                }
                if (current.value() instanceof ReadableField) {
                    ReadableField<?> read = (ReadableField<?>) current.value();
                    validationTarget = read.readValue(validationTarget);
                }
                current = current.next();
            }
        }
    }

    @SuppressWarnings("rawtypes")
    private <T> void validatePath(ResourceRegistry registry, Errors errors, ResourcePath path, T target, MappedField<?> mappedField) {
        if (target != null) {
            if (log.isDebugEnabled()) {
                log.debug("Validating {} {}.{}", path, target.getClass(), mappedField.getBeanName());
            }
            Set<ConstraintViolation<T>> violations = validator.validateProperty(target, mappedField.getBeanName());
            if (CollectionUtils.isNotEmpty(violations)) {
                Object value = mappedField.readValue(target);
                if (log.isDebugEnabled()) {
                    log.debug("Validation Error {} {}.{} = \"{}\"", path, target.getClass(), mappedField.getBeanName(), value);
                }
                addViolationErrors(errors, violations, path, value);
            }

            Object o = mappedField.readValue(target);
            if (o != null) {
                Type type = mappedField.getType();
                if (mappedField instanceof IterableField) {
                    type = ((IterableField) mappedField).getGenericType();
                }
                MappedClass<?> m = registry.getMappedClass(type);
                if (m != null) {
                    if (o instanceof Iterable) {
                        if (path.lastValue() instanceof IndexPathValue) {
                            validateObject(registry, errors, path, m, path.getValue(target));
                        } else {
                            Iterator<?> it = ((Iterable) o).iterator();
                            int i = 0;
                            while (it.hasNext()) {
                                validateObject(registry, errors, path.append(i++), m, it.next());
                            }
                        }
                    } else {
                        validateObject(registry, errors, path, m, o);
                    }
                }
            }
        }
    }

    private void validateObject(ResourceRegistry registry, Errors errors, ResourcePath base, MappedClass<?> mapping, Object target) {
        for (MappedField<?> mappedField : mapping.getAttributes()) {
            validatePath(registry, errors, base.append(mappedField), target, mappedField);
        }
    }

    protected <T, ID extends Serializable> void addViolationErrors(Errors errors, Set<ConstraintViolation<T>> violations,
            ResourcePath path, Object value) {
        if (violations != null) {
            for (ConstraintViolation<?> v : violations) {
                RequestError.Builder err = RequestError.builder()
                        .source(path)
                        .title("Invalid field value")
                        .detail("{0} {1}", path.getApiPath(), v.getMessage())
                        .meta("value", value);
                ConstraintDescriptor<?> constraint = v.getConstraintDescriptor();
                if (constraint != null) {
                    Annotation ann = constraint.getAnnotation();
                    if (ann != null) {
                        addMeta(err, ann, constraint, value);
                    }
                }

                errors.addError(err);
            }
        }
    }

    void addMeta(RequestError.Builder err, Annotation ann, ConstraintDescriptor<?> constraint, Object value) {
        if (ann instanceof Max) {
            err.meta("actualLength", length(value));
            err.meta("max", constraint.getAttributes().get("value"));
        } else if (ann instanceof Min) {
            err.meta("actualLength", length(value));
            err.meta("min", constraint.getAttributes().get("value"));
        } else if (ann instanceof Size) {
            err.meta("actualLength", length(value));
            err.meta("min", constraint.getAttributes().get("min"));
            err.meta("max", constraint.getAttributes().get("max"));
        }
    }

    @Override
	public <T, ID extends Serializable> boolean accepts(Resource<T, ID> resource) {
		return resource.getType() instanceof Class;
	}

}
