package com.github.restup.test.serializer;

import com.google.gson.Gson;

public class GsonSerializer implements SerializationProvider {

    private static Gson gson;

    private static Gson getGson() {
        if (gson == null) {
            gson = new Gson();
        }
        return gson;
    }

    public static String convertToString(Object o) {
        return convertToString(null, o);
    }

    public static String convertToString(Gson gson, Object o) {
        try {
            if (gson == null) {
                gson = getGson();
            }
            return gson.toJson(o);
        } catch (Throwable e) {
            throw new RuntimeException("Unable to serialize value", e);
        }
    }


}
