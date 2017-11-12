package com.github.restup.controller.model;

import com.github.restup.registry.settings.ControllerMethodAccess;

public enum HttpMethod {
    GET, POST, PATCH, PUT, DELETE, OPTIONS; //, HEAD, TRACE, CONNECT;


    public static HttpMethod of(String method) {
        if (method != null) {
            for (HttpMethod m : values()) {
                if (m.name().equalsIgnoreCase(method)) {
                    return m;
                }
            }
        }
        return null;
    }

    /**
     * @param method
     * @return true if method is supported for multiple ids
     */
    public boolean supportsAccessByIds(ControllerMethodAccess access) {
        switch (this) {
            case GET:
                return !access.isGetByIdsDisabled();
            case PATCH:
                return !access.isPatchByIdsDisabled();
            case DELETE:
                return !access.isDeleteByIdsDisabled();
            default:
                return false;
        }
    }

    /**
     * @param method
     * @return true if method is supported for single ids
     */
    public boolean supportsItemOperation(ControllerMethodAccess access) {
        switch (this) {
            case GET:
                return !access.isGetByIdDisabled();
            case PATCH:
                return !access.isPatchByIdDisabled();
            case DELETE:
                return !access.isDeleteByIdDisabled();
            case PUT:
                return !access.isUpdateByIdDisabled();
            default:
                return false;
        }
    }

    public boolean supportsCollectionOperation(ControllerMethodAccess access) {
        switch (this) {
            case GET:
                return !access.isListDisabled();
            case POST:
                return !access.isCreateDisabled();
            case PATCH:
                return !access.isPatchByQueryDisabled();
            case DELETE:
                return !access.isDeleteByQueryDisabled();
            default:
                return false;
        }
    }

    public boolean supportsMultiple(ControllerMethodAccess access) {
        switch (this) {
            case PATCH:
                return !access.isPatchMultipleDisabled();
            case POST:
                return !access.isCreateMultipleDisabled();
            case PUT:
                return !access.isUpdateMultipleDisabled();
            default:
                return false;
        }
    }

    public boolean requiresData() {
        switch (this) {
            case PATCH:
                return true;
            case POST:
                return true;
            case PUT:
                return true;
            default:
                return false;
        }
    }
}