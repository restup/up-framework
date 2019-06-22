package com.data;


import com.github.restup.annotations.ApiName;
import com.github.restup.annotations.Resource;
import com.github.restup.annotations.field.ClientGeneratedIdentifier;
import com.github.restup.annotations.model.CreateStrategy;
import com.github.restup.annotations.model.DeleteStrategy;
import com.github.restup.annotations.model.UpdateStrategy;

/**
 * To test ACCEPT strategies
 */
@ApiName("message")
@Resource(createStrategy = CreateStrategy.NO_CONTENT,
    updateStrategy = UpdateStrategy.NO_CONTENT,
    deleteStrategy = DeleteStrategy.NO_CONTENT)
public class Message {

    @ClientGeneratedIdentifier
    private final String id;
    private final String message;

    public Message(String id, String message) {
        this.id = id;
        this.message = message;
    }

    Message() {
        // for Jackson deserialization
        this(null, null);
    }

    public String getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }
}
