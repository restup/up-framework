package com.github.restup.test.serializer;

public class AutoDetectConstants {

    public static final boolean JACKSON2_EXISTS =
            classesExist("com.fasterxml.jackson.databind.ObjectMapper",
                    "com.fasterxml.jackson.core.JsonGenerator");

    public static final boolean GSON_EXISTS =
            classesExist("com.google.gson.Gson");


    private AutoDetectConstants() {

    }

    static boolean classesExist(String... classes) {
        try {
            for (String clazz : classes) {
                AutoDetectConstants.class.getClassLoader().loadClass(clazz);
            }
        } catch (Throwable e) {
            return false;
        }
        return true;
    }

}
