package com.github.restup.controller.request.parser.params;

import java.util.Iterator;
import java.util.NoSuchElementException;
import org.apache.commons.collections4.iterators.ArrayIterator;

public class DelimitedParameterIterator implements Iterator<String> {

    private final String delimiter;
    private final ArrayIterator<String> parameterIterator;
    private ArrayIterator<String> splitIterator;

    public DelimitedParameterIterator(String delimiter, String... arr) {
        this.delimiter = delimiter;
        parameterIterator = nullSafeIterator(arr);
    }

    private static ArrayIterator<String> nullSafeIterator(String[] arr) {
        return arr == null ? new ArrayIterator<>(new String[0]) : new ArrayIterator<>(arr);
    }

    @Override
    public boolean hasNext() {
        try {
            return getSplitIterator().hasNext();
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    @Override
    public String next() {
        return getSplitIterator().next();
    }

    private ArrayIterator<String> getSplitIterator() {
        if (splitIterator == null || !splitIterator.hasNext()) {
            splitIterator = nextSplitIterator();
        }
        return splitIterator;
    }

    private ArrayIterator<String> nextSplitIterator() {
        String value = parameterIterator.next();
        while (value == null) {
            // will either throw NoSuchElementException or return next non null value
            value = parameterIterator.next();
        }
        String[] arr = value.split(delimiter);
        if (arr.length < 1) {
            return nextSplitIterator();
        }

        return new ArrayIterator<>(value.split(delimiter));
    }
}
