package com.github.restup.jackson.mixins;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.restup.jackson.serializer.LinksSerializer;

@JsonSerialize(using = LinksSerializer.class)
public class LinksResultMixin {

}
