package com.github.restup.jackson.mixins;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.restup.jackson.serializer.JsonResultSerializer;

@JsonSerialize(using = JsonResultSerializer.class)
public class JsonResultMixin {

}
