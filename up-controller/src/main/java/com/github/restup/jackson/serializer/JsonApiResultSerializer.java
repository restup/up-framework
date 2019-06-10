package com.github.restup.jackson.serializer;

import static com.github.restup.jackson.serializer.LinksSerializer.writeLinksObject;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.github.restup.controller.linking.Link;
import com.github.restup.controller.model.result.JsonApiResult;
import com.github.restup.mapping.fields.MappedField;
import com.github.restup.path.DataPathValue;
import com.github.restup.path.MappedFieldPathValue;
import com.github.restup.path.PathValue;
import com.github.restup.registry.Resource;
import com.github.restup.registry.ResourceRelationship;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections4.CollectionUtils;

/**
 * Custom serialization for JSON API content.
 */
public class JsonApiResultSerializer extends NegotiatedResultSerializer<JsonApiResult> {

    /*TODO
     * add top level linking
     */
    @Override
    protected void writeLinking(JsonApiResult result, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        List<Link> links = result.getTopLevelLinks();
        writeLinks(jgen, links);
    }

    /*TODO
     * Identifier is always a string
     */
    @Override
    protected Object writeIdentifier(JsonGenerator jgen, String fieldName, Object id) throws IOException {
        String result = String.valueOf(id);
        jgen.writeStringField(fieldName, result);
        return result;
    }

    /*TODO
     * writes json api Resource objects with id, type, attributes, relationships, links.
     */
    @Override
    protected void writeResourceObject(Resource<?, ?> resource, Map<PathValue, ?> paths, Object data, JsonApiResult result, JsonGenerator jgen, SerializerProvider provider) throws Exception {
        jgen.writeStartObject();
        Object[] id = writeIdentifier(resource, data, result, jgen, provider);
        writeType(resource, data, result, jgen, provider);

        jgen.writeFieldName(JsonApiResult.ATTRIBUTES);
        jgen.writeStartObject();
        // TODO allow configuration of behavior of whether relationship ids are rendered
        // Although has-one foreign keys (e.g. author_id) are often stored internally alongside other information to be represented in a resource object,
        // these keys SHOULD NOT appear as attributes.
        // but there might be cases where it is useful...
        super.writeAttributes(resource, paths, data, result, jgen, provider, false);
        jgen.writeEndObject();

        writeResourceRelationships(resource, paths, data, result, jgen, provider);

        writeLinks(jgen, result.getLinks(resource, id));

        //TODO meta

        jgen.writeEndObject();
    }

    protected void writeResourceRelationships(Resource<?, ?> resource, Map<PathValue, ?> paths, Object data, JsonApiResult result, JsonGenerator jgen,
            SerializerProvider provider) throws Exception {
        //TODO toMany relationshps
        Collection<ResourceRelationship<?,?,?,?>> relationships = resource.getRelationshipsTo();
        if (hasRelationships(paths) || CollectionUtils.isNotEmpty(relationships)) {
            jgen.writeFieldName(JsonApiResult.RELATIONSHIPS);
            jgen.writeStartObject();
            Object[] id = getIdentityData(resource, data);
            // From relationships... these can be controlled through sparse requests
            for (Map.Entry<PathValue, ?> e : paths.entrySet()) {
                PathValue pv = e.getKey();
                if (MappedFieldPathValue.isRelationshipField(pv)) {
                    MappedFieldPathValue<?> mappedFieldPathValue = (MappedFieldPathValue<?>) pv;
                    MappedField<?> mappedField = mappedFieldPathValue.getMappedField();

                    Resource<?, ?> rel = resource.getRegistry().getResource(mappedField.getRelationshipResource(resource.getRegistry()));
                    Object value = mappedFieldPathValue.readValue(data);

                    // add the relationship key and start the relationship object
                    jgen.writeFieldName(MappedField.getRelationshipName(mappedField, rel));
                    jgen.writeStartObject();

                    // write the resource linkage "data" object
                    writeResourceIdentifierObject(jgen, rel, value);

                    // write relationship links
                    writeLinks(jgen, result.getRelationshipLinks(rel, id));

                    jgen.writeEndObject();
                }
            }
            //TODO should there be a sparse request to omit to relationships?
            // to relationships
            if (CollectionUtils.isNotEmpty(relationships)) {
                for (ResourceRelationship<?,?,?,?> relationship : relationships) {
                    jgen.writeFieldName(ResourceRelationship.getRelationshipNameForToResource(relationship));
                    jgen.writeStartObject();

                    // write relationship links
                    writeLinks(jgen, result.getRelationshipLinks(relationship.getFrom(), id,
                        relationship.getType(resource)));

                    jgen.writeEndObject();

                }
            }
            jgen.writeEndObject();
        }
    }

    private Object[] getIdentityData(Resource<?, ?> resource, Object data) {
        Object[] result = new Object[resource.getIdentityField().length];
        for (int i = 0; i < resource.getIdentityField().length; i++) {
            result[i] = resource.getIdentityField()[i].readValue(data);
        }
        return result;
    }

    /*TODO
     * Writes the resource identifier object for a relationship
     * <pre>
     * "data": { "type": "people", "id": "9" }
     * </pre>
     */
    protected Object writeResourceIdentifierObject(JsonGenerator jgen, Resource<?,?> resource, Object value) throws Exception {
        jgen.writeFieldName(DataPathValue.DATA);
        String type = resource.getName();
        Object id = null;
        if (value instanceof Iterable) {
            jgen.writeStartArray();
            Iterator<?> it = ((Iterable<?>) value).iterator();
            while (it.hasNext()) {
                writeResourceIdentifierFields(jgen, type, it.next());
            }
            jgen.writeEndArray();
        } else {
            jgen.writeStartObject();
            id = writeResourceIdentifierFields(jgen, type, value);
            jgen.writeEndObject();
        }
        return id;
    }

    protected Object writeResourceIdentifierFields(JsonGenerator jgen, String type, Object id) throws Exception {
        jgen.writeStringField(JsonApiResult.TYPE, type);
        return writeIdentifier(jgen, JsonApiResult.ID, id);
    }

    protected boolean hasRelationships(Map<PathValue, ?> paths) {
        for (Map.Entry<PathValue, ?> e : paths.entrySet()) {
            PathValue pv = e.getKey();
            if (MappedFieldPathValue.isRelationshipField(pv)) {
                return true;
            }
        }
        return false;
    }

    protected void writeLinks(JsonGenerator jgen, Collection<Link> links) throws IOException {
        if (CollectionUtils.isNotEmpty(links)) {
            jgen.writeFieldName(JsonApiResult.LINKS);
            writeLinksObject(jgen, links);
        }
    }

}
