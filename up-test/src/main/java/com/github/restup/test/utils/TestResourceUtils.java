package com.github.restup.test.utils;

import java.net.URL;
import org.apache.commons.lang3.StringUtils;

public class TestResourceUtils {

    public static String getRelativePath(Class<?> relativeTo, boolean includeClassName, String fileExtension, String... toAppend) {

        String baseName = includeClassName ? relativeTo.getName() : relativeTo.getPackage().getName();

        StringBuilder sb = new StringBuilder("/");
        sb.append(baseName.replaceAll("\\.", "/"));
        String last = null;
        for (String path : toAppend) {
            if (path != null) {
                sb.append("/");
                sb.append(path);
                last = path;
            }
        }
        if (!last.contains(".")) {
            if (StringUtils.isNotBlank(fileExtension)) {
                if (fileExtension.charAt(0) != '.') {
                    sb.append(".");
                }
                sb.append(fileExtension);
            }
        }
        return sb.toString();
    }

    public static URL getResource(Class<?> clazz, String fileName) {
        String path = getRelativePath(clazz, false, null, fileName);
        return getResource(path);
    }

    public static URL getResource(String path) {
        return ClassLoader.class.getResource(path);
    }

}
