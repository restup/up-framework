package com.data;


import com.github.restup.annotations.ApiName;
import com.github.restup.annotations.Plural;
import com.github.restup.annotations.Resource;
import com.github.restup.annotations.field.ClientGeneratedIdentifier;
import com.github.restup.annotations.model.CreateStrategy;
import com.github.restup.annotations.model.DeleteStrategy;
import com.github.restup.annotations.model.UpdateStrategy;

/**
 * To test ACCEPT strategies
 */
@ApiName("data")
@Plural("data")
@Resource(createStrategy = CreateStrategy.ACCEPTED,
    updateStrategy = UpdateStrategy.ACCEPTED,
    deleteStrategy = DeleteStrategy.ACCEPTED)
public class DataPoint {


    @ClientGeneratedIdentifier
    private final String id;
    private final String label;
    private final String value;

    public DataPoint(String id, String label, String value) {
        this.id = id;
        this.label = label;
        this.value = value;
    }

    DataPoint() {
        // for Jackson deserialization
        this(null, null, null);
    }

    public String getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public String getValue() {
        return value;
    }
}
