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

    @JsonProperty("id")
    String getId();

    @JsonProperty("code")
    String getCode();

    @JsonProperty("title")
    String getTitle();

    @JsonProperty("detail")
    String getDetail();

    @JsonProperty("source")
    ErrorSource getSource();

    @JsonProperty("meta")
    Object getMeta();

    @JsonProperty("status")
    String getStatus();

}
