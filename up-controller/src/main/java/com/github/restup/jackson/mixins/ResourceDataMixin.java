package com.github.restup.jackson.mixins;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.restup.service.model.ResourceData;

public interface ResourceDataMixin<T> extends ResourceData<T> {

    @Override
    @JsonProperty("data")
    T getData();

}
