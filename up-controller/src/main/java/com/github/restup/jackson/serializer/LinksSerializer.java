package com.github.restup.jackson.serializer;

import java.io.IOException;
import java.util.Collection;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.github.restup.controller.linking.BasicLink;
import com.github.restup.controller.linking.Link;
import com.github.restup.controller.linking.LinksResult;

public class LinksSerializer extends JsonSerializer<LinksResult> {

    protected static void writeLinksObject(JsonGenerator jgen, Collection<Link> links) throws IOException {
        jgen.writeStartObject();
        for (Link link : links) {
            if (link instanceof BasicLink) {
                jgen.writeStringField(link.getName(), link.getHref());
            } else {
                jgen.writeObjectField(link.getName(), link);
            }
        }
        jgen.writeEndObject();
    }

    @Override
    public void serialize(LinksResult result, JsonGenerator jgen, SerializerProvider provider)
            throws IOException, JsonProcessingException {
        writeLinksObject(jgen, result.getLinks());
    }

}
