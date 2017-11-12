package com.github.restup.test.resource;

import com.github.restup.test.ApiExecutor;
import com.github.restup.test.utils.TestResourceUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class RelativeTestResource implements ResourceContents {
    public static final String REQUESTS = "requests";
    public static final String RESPONSES = "responses";
    public static final String RESULTS = "results";
    public static final String DUMPS = "dumps";
    private final static Logger log = LoggerFactory.getLogger(RelativeTestResource.class);
    private final Class<?> relativeTo;
    private final String dir;
    private final String fileName;
    private final String fileExtension;
    private final String path;

    public RelativeTestResource(Class<?> relativeTo, String dir, String fileName, String fileExtension) {
        this.relativeTo = relativeTo;
        this.dir = dir;
        this.fileName = fileName;
        this.fileExtension = fileExtension;

        StringBuilder sb = new StringBuilder();

        // use relative paths.  Using classpath: would be nicer, but the problem can be that
        // Eclipse copies the files to target/test-classes/.. so there is potential the *source*
        // resource gets out of sync with the *classpath* resource... this can make for some
        // confusing debugging if you do not keep this in mind.
        sb.append(System.getProperty("user.dir"));
        sb.append("/src/test/resources");
        sb.append(TestResourceUtils.getRelativePath(relativeTo, true, fileExtension, dir, fileName));
        this.path = sb.toString();
    }

    public RelativeTestResource(Class<?> relativeTo, String fileName) {
        this(relativeTo, null, fileName, null);
    }

    /**
     *
     * @return first non Up! test element from the stack
     */
    public static StackTraceElement getCallingStackElement() {
        int i = 2;
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        StackTraceElement el = stack[i];
        while (i < stack.length
                && el.getClassName().startsWith(ApiExecutor.class.getPackage().getName())) {
            el = stack[i++];
        }
        return el;
    }

    public static Class<?> getClassFromStack() {
        try {
            return Class.forName(getCallingStackElement().getClassName());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Class Not Found", e);
        }
    }

    private static RelativeTestResource resource(Class<?> relativeTo, String dir, String fileName) {
        return new RelativeTestResource(relativeTo, dir, fileName, "json");
    }

    private static RelativeTestResource resource(String dir, String fileName) {
        Class<?> relativeTo = getClassFromStack();
        return resource(relativeTo, dir, fileName);
    }

    public static RelativeTestResource dump(Class<?> relativeTo, String fileName) {
        return resource(relativeTo, DUMPS, fileName);
    }

    public static RelativeTestResource dump(String fileName) {
        return resource(DUMPS, fileName);
    }

    public static RelativeTestResource result(String fileName) {
        return resource(RESULTS, fileName);
    }

    public static RelativeTestResource request(String fileName) {
        return resource(REQUESTS, fileName);
    }

    public static RelativeTestResource response(String fileName) {
        return resource(RESPONSES, fileName);
    }

    public String getContentAsString() {
        byte[] result = getContentAsByteArray();
        return result == null ? null : new String(result);
    }

    public byte[] getContentAsByteArray() {
        if (!exists()) {
            return null;
        } else {
            log.debug("Loading contents from {}", path);
            try {
                return IOUtils.toByteArray(new FileInputStream(getFile()));
            } catch (IOException e) {
                log.error("Unable to load contents from " + path, e);
                throw new RuntimeException("Unable to read file", e);
            }
        }
    }

    public void writeResult(byte[] body) {
        try {
            log.info("Writing results to {}", path);
            FileUtils.writeByteArrayToFile(getFile(), body);
        } catch (IOException e) {
            throw new RuntimeException("Unable to write to " + path, e);
        }
    }

    public boolean exists() {
        return getFile().exists();
    }

    private File getFile() {
        return new File(path);
    }

    public String getPath() {
        return path;
    }

    public String getDir() {
        return dir;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public String getFileName() {
        return fileName;
    }

    public Class<?> getRelativeTo() {
        return relativeTo;
    }

}
