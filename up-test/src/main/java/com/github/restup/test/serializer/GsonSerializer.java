package com.github.restup.test.serializer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GsonSerializer implements ResultSerializer {

    private static Gson instance;
    private Gson gson;
    
    public GsonSerializer(Gson gson) {
        this.gson = gson;
    }
    
    public GsonSerializer() {
        this(getGson());
    }

    public GsonSerializer(GsonBuilder gson) {
        this(gson.create());
    }

    private static Gson getGson() {
        if (instance == null) {
            instance = new Gson();
        }
        return instance;
    }

    @Override
    public String convertToString(Object o) {
        try {
            return gson.toJson(o);
        } catch (Throwable e) {
            throw new AssertionError("Unable to serialize value", e);
        }
    }

}
