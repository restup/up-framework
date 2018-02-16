package com.github.restup.path;

/**
 * An item of a {@link ResourcePath} representing a single portion of the full path
 */
public interface PathValue {

    /**
     * @return the api name of the path
     */
    String getApiPath();

    /**
     * @return the bean name of the path
     */
    String getBeanPath();

    /**
     * @return the persisted name of the path
     */
    String getPersistedPath();

    /**
     * @param instance type to confirm is supported by the {@link PathValue}
     * @return true if the {@link PathValue} supports the type specified, false otherwise
     */
    boolean supportsType(Class<?> instance);

    /**
     * @return true if the path represents a reserved path (type, id, data, included, linking, etc)
     */
    default boolean isReservedPath() {
        return false;
    }

    static InvalidPathValue invalid(String field) {
        return new InvalidPathValue(field);
    }

    static DataPathValue data() {
        return DataPathValue.getInstance();
    }

}
