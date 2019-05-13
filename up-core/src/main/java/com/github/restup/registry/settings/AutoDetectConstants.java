package com.github.restup.registry.settings;

import static com.github.restup.util.ReflectionUtils.classesExist;

public class AutoDetectConstants {

    public static final boolean JACKSON2_EXISTS =
        classesExist("com.fasterxml.jackson.databind.ObjectMapper",
            "com.fasterxml.jackson.core.JsonGenerator");

    public static final boolean GSON_EXISTS = classesExist("com.google.gson.Gson");

    private AutoDetectConstants() {

    }
}
