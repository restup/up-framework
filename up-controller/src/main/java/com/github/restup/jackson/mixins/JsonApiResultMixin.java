package com.github.restup.jackson.mixins;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.restup.jackson.serializer.JsonApiResultSerializer;

@JsonSerialize(using = JsonApiResultSerializer.class)
public class JsonApiResultMixin {

}
