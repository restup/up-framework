package com.github.restup.jackson.serializer;

import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.github.restup.controller.model.result.NegotiatedResult;
import com.github.restup.errors.ErrorCode;
import com.github.restup.errors.RequestError;
import com.github.restup.mapping.fields.MappedField;
import com.github.restup.mapping.fields.ReadableField;
import com.github.restup.path.DataPathValue;
import com.github.restup.path.EmbeddedResourcePathValue;
import com.github.restup.path.IndexPathValue;
import com.github.restup.path.MappedFieldPathValue;
import com.github.restup.path.PathValue;
import com.github.restup.registry.Resource;
import com.github.restup.service.model.response.PagedResult;
import com.github.restup.service.model.response.ReadResult;
import com.github.restup.service.model.response.RelatedResourceResult;
import com.github.restup.service.model.response.ResourceResult;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class NegotiatedResultSerializer<T extends NegotiatedResult> extends JsonSerializer<T> {

    private final static Logger log = LoggerFactory.getLogger(JsonResultSerializer.class);

    @Override
    public void serialize(T result, JsonGenerator jgen, SerializerProvider provider)
            throws IOException, JsonProcessingException {
        if (accept(result)) {
            try {
                jgen.writeStartObject();
                writeLinking(result, jgen, provider);
                writeData(result, jgen, provider);
                writeIncluded(result, jgen, provider);
                jgen.writeEndObject();
            } catch (Exception e) {
                log.error("Resource Object Serialization error", e);
                RequestError.error(result.getRequest().getResource(), e)
                        .code(ErrorCode.SERIALIZATION_ERROR)
                        .throwError();
            }
        } else {
            jgen.writeObject(result.getResult());
        }
    }

    protected boolean accept(T result) {
        return result.getResult() instanceof ResourceResult;
    }

    /*TODO
     * Writes pagination details without linking
     */
    protected void writeLinking(T result, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        if (result.getResult() instanceof PagedResult) {
            PagedResult<?> paged = (PagedResult<?>) result.getResult();
            writeIfNotNull(jgen, "limit", paged.getLimit());
            writeIfNotNull(jgen, "offset", paged.getOffset());
            writeIfNotNull(jgen, "total", paged.getTotal());
        }
    }

    protected void writeIncluded(T result, JsonGenerator jgen, SerializerProvider provider)
        throws Exception {
        if (result.getResult() instanceof ResourceResult) {
            ResourceResult resourceResult = (ResourceResult) result.getResult();
            if (isNotEmpty(resourceResult.getRelatedResourceResults())) {
                jgen.writeArrayFieldStart("included");
                List<RelatedResourceResult<?, ?>> list = resourceResult.getRelatedResourceResults();
                for (RelatedResourceResult<?, ?> relatedResourceResult : list) {
                    ReadResult<List<?>> r = (ReadResult) relatedResourceResult.getResult();
                    if (isNotEmpty(r.getData())) {
                        Resource<?, ?> resource = relatedResourceResult.getResource();
                        Map<PathValue, ?> paths = result.getMappedPaths(resource);
                        for (Object data : r.getData()) {
                            writeObject(resource, paths, data, result, jgen, provider);
                        }
                    }
                }
                jgen.writeEndArray();
            }
        }
    }

    /*TODO
     * write "data" field
     */
    protected void writeData(T result, JsonGenerator jgen, SerializerProvider provider) throws Exception {
        if (!result.isCount()) {
            Resource<?, ?> resource = result.getResource();
            writeObjectField(DataPathValue.DATA, resource, result.getMappedPaths(),
                result.getData(), result, jgen, provider);
        }
    }

    /*TODO
     * writes fieldName, then calls
     * {@link #writeObject(Resource, Map, Object, NegotiatedResult, JsonGenerator, SerializerProvider)}
     */
    protected void writeObjectField(String fieldName, Resource<?, ?> resource, Map<PathValue, ?> paths, Object data, T result, JsonGenerator jgen, SerializerProvider provider) throws Exception {
        jgen.writeFieldName(fieldName);
        writeObject(resource, paths, data, result, jgen, provider);
    }

    /*TODO
     * Writes an object (value only, not field), checking for arrays and nulls.
     */
    protected void writeObject(Resource<?, ?> resource, Map<PathValue, ?> paths, Object data, T result, JsonGenerator jgen, SerializerProvider provider) throws Exception {
        if (data == null) {
            jgen.writeNull();
        } else if (data instanceof Iterable) {
            writeIterable(resource, paths, (Iterable<?>) data, result, jgen, provider);
        } else if (data instanceof Object[]) {
            writeArray(resource, paths, (Object[]) data, result, jgen, provider);
        } else if (resource != null) {
            writeResourceObject(resource, paths, data, result, jgen, provider);
        } else {
            jgen.writeStartObject();
            writeAttributes(resource, paths, data, result, jgen, provider, true);
            jgen.writeEndObject();
        }
    }

    protected void writeResourceObject(Resource<?, ?> resource, Map<PathValue, ?> paths, Object data, T result, JsonGenerator jgen, SerializerProvider provider) throws Exception {
        jgen.writeStartObject();
        writeIdentifier(resource, data, result, jgen, provider);
        writeType(resource, data, result, jgen, provider);
        writeAttributes(resource, paths, data, result, jgen, provider, true);
        jgen.writeEndObject();
    }

    /*TODO
     * Write an array from an iterable object
     */
    protected void writeIterable(Resource<?, ?> resource, Map<PathValue, ?> paths, Iterable<?> data, T result,
            JsonGenerator jgen, SerializerProvider provider) throws Exception {
        jgen.writeStartArray();
        boolean indexed = writeIndexed(resource, paths, data, result, jgen, provider);
        if (!indexed) {
            Iterator<?> it = data.iterator();
            while (it.hasNext()) {
                writeObject(resource, paths, it.next(), result, jgen, provider);
            }
        }
        jgen.writeEndArray();
    }

    /*TODO
     * Write an array from an array object
     */
    protected void writeArray(Resource<?, ?> resource, Map<PathValue, ?> paths, Object[] data, T result,
            JsonGenerator jgen, SerializerProvider provider) throws Exception {
        jgen.writeStartArray();
        boolean indexed = writeIndexed(resource, paths, data, result, jgen, provider);
        if (!indexed) {
            for (Object o : data) {
                writeObject(resource, paths, o, result, jgen, provider);
            }
        }
        jgen.writeEndArray();
    }

    /*TODO
     * Write all indexes of path (assumes data is an array or iterable if so)
     */
    protected boolean writeIndexed(Resource<?, ?> resource, Map<PathValue, ?> paths, Object data, T result,
            JsonGenerator jgen, SerializerProvider provider) throws Exception {
        boolean indexed = false;
        for (Map.Entry<PathValue, ?> e : paths.entrySet()) {
            PathValue pv = e.getKey();
            if (pv instanceof IndexPathValue && e.getValue() instanceof Map) {
                IndexPathValue i = (IndexPathValue) pv;
                Object value = i.readValue(data);
                writeObject(getResource(pv), asMap(e.getValue()), value, result,
                    jgen, provider);
                indexed = true;
            }
        }
        return indexed;
    }

    /*TODO
     * writes the "type" field
     */
    protected void writeType(Resource<?, ?> resource, Object data, T result, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        // TODO check if type was explicitly requested to be removed
        jgen.writeStringField("type", resource.getName());
    }

    /*TODO
     * writes the "id" fieldName, calls {@link #writeIdentifier(JsonGenerator, String, Object)} to write value
     */
    protected Object[] writeIdentifier(Resource<?, ?> resource, Object data, T result,
        JsonGenerator jgen, SerializerProvider provider) throws IOException {
        // TODO check if id was explicitly requested to be removed
        Object[] id = new Object[resource.getIdentityField().length];
        for (int i = 0; i < resource.getIdentityField().length; i++) {
            MappedField<?> field = resource.getIdentityField()[i];
            id[i] = field.readValue(data);
            writeIdentifier(jgen, field.getApiName(), id);
        }
        return id;
    }

    /*TODO
     * writes the id value
     */
    protected Object writeIdentifier(JsonGenerator jgen, String fieldName, Object id) throws IOException {
        jgen.writeObjectField(fieldName, id);
        return id;
    }

    /*TODO
     * writes object attributes
     */
	protected void writeAttributes(Resource<?, ?> resource, Map<PathValue, ?> paths, Object data, T result, JsonGenerator jgen, SerializerProvider provider, boolean writeRelationships) throws Exception {
        for (Map.Entry<PathValue, ?> e : paths.entrySet()) {
            PathValue pv = e.getKey();
            if (pv instanceof ReadableField && (writeRelationships || !MappedFieldPathValue.isRelationshipField(pv))) {
                ReadableField<?> readable = (ReadableField<?>) pv;
                String fieldName = pv.getApiPath();
                Object value = readable.readValue(data);

                if (e.getValue() == null) {
                    BeanPropertyWriter bpw = findBeanPropertyWriter(data, jgen, provider);
                    if (bpw == null) {
                        JsonSerializer<Object> s = findCustomSerializer(pv, data, jgen, provider);
                        if (s == null) {
                            jgen.writeObjectField(fieldName, value);
                        } else {
                            s.serialize(value, jgen, provider);
                        }
                    } else {
                        bpw.serializeAsField(data, jgen, provider);
                    }
                } else if (e.getValue() instanceof Map) { // always should be
                    Resource<?, ?> pathResource = getResource(pv);
                    writeObjectField(fieldName, pathResource, asMap(e.getValue()), value,
                        result, jgen, provider);
                }
            }
        }
    }

    private Map<PathValue, ?> asMap(Object value) {
		return (Map) value;
	}

	protected Resource<?, ?> getResource(PathValue pv) {
        if (pv instanceof EmbeddedResourcePathValue) {
            return ((EmbeddedResourcePathValue) pv).getResource();
        }
        return null;
    }

    /*TODO
     * writes field + value if value is not null
     */
    protected void writeIfNotNull(JsonGenerator jgen, String fieldName, Object value) throws IOException {
        if (value != null) {
            jgen.writeObjectField(fieldName, value);
        }
    }

    private BeanPropertyWriter findBeanPropertyWriter(Object data, JsonGenerator jgen, SerializerProvider provider) {
        // TODO Find bean property writer to serialize using Jackson default settings
        return null;
    }

    private JsonSerializer<Object> findCustomSerializer(PathValue pv, Object data, JsonGenerator jgen,
            SerializerProvider provider) {
        // TODO ensure custom serializers are used
        return null;
    }
}
