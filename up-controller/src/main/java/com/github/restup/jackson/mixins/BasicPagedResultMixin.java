package com.github.restup.jackson.mixins;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;


@JsonInclude(Include.NON_EMPTY)
@JsonAutoDetect(fieldVisibility = Visibility.NONE, getterVisibility = Visibility.NONE,
        setterVisibility = Visibility.NONE, isGetterVisibility = Visibility.NONE)
public interface BasicPagedResultMixin<T> extends ResourceDataMixin<T> {

    @JsonProperty("limit")
    Integer getLimit();

    @JsonProperty("offset")
    Integer getOffset();

    @JsonProperty("total")
    Long getTotal();

}
