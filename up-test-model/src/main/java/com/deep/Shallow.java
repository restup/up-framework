package com.deep;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import javax.validation.constraints.NotNull;

public class Shallow extends BaseObject {

    private static final AtomicLong sequence = new AtomicLong(new Random().nextInt(1000));
    @NotNull
    private Deep deep;
    private List<Deep> deeps;

    public static Shallow graph() {
        AtomicInteger i = new AtomicInteger(0);
        Shallow shallow = init(i, new Shallow());
        shallow.setDeep(deep(i));
        shallow.setDeeps(asList(deep(i), deep(i)));
        return shallow;
    }

    @SuppressWarnings("unchecked")
    static <T> List<T> asList(T... items) {
        List<T> list = new ArrayList<>();
        for ( T t : items ) {
            list.add(t);
        }
        return list;
    }

    public static Deep deep(AtomicInteger i) {
        Deep d = new Deep();
        d.setDeeper(deeper(i));
        d.setDeepers(asList(deeper(i), deeper(i)));
        return init(i, d);
    }

    public static Deeper deeper(AtomicInteger i) {
        Deeper d = new Deeper();
        d.setDeepest(deepest(i));
        d.setDeepests(asList(deepest(i), deepest(i)));
        return init(i, d);
    }

    public static Deepest deepest(AtomicInteger i) {
        Deepest d = new Deepest();
        return init(i, d);
    }

    private static <T extends BaseObject> T init(AtomicInteger id, T base) {
        base.setId(sequence.incrementAndGet());
        base.setDepth(10);
        base.setName(String.valueOf((char) (id.get() % 26 + 'A')));
        id.incrementAndGet();
        return base;
    }

    public Deep getDeep() {
        return deep;
    }

    public void setDeep(Deep deep) {
        this.deep = deep;
    }

    public List<Deep> getDeeps() {
        return deeps;
    }

    public void setDeeps(List<Deep> deeps) {
        this.deeps = deeps;
    }

}
