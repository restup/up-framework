package com.github.restup.jackson.mixins;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.restup.errors.ErrorSource;
import com.github.restup.errors.RequestError;

@JsonInclude(Include.NON_EMPTY)
@JsonAutoDetect(fieldVisibility = Visibility.NONE, getterVisibility = Visibility.NONE,
        setterVisibility = Visibility.NONE, isGetterVisibility = Visibility.NONE)
public interface RequestErrorMixin extends RequestError {

    @Override
    @JsonProperty("id")
    String getId();

    @Override
    @JsonProperty("code")
    String getCode();

    @Override
    @JsonProperty("title")
    String getTitle();

    @Override
    @JsonProperty("detail")
    String getDetail();

    @Override
    @JsonProperty("source")
    ErrorSource getSource();

    @Override
    @JsonProperty("meta")
    Object getMeta();

    @Override
    @JsonProperty("status")
    String getStatus();

}
