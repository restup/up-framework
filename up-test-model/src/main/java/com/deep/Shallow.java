package com.deep;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.validation.constraints.NotNull;

public class Shallow extends BaseObject {

    private static final AtomicLong id = new AtomicLong(new Random().nextInt(10000));
    @NotNull
    private Deep deep;
    private List<Deep> deeps;

    public static Shallow graph() {
        int i = 0;
        Shallow shallow = init(i++, new Shallow());
        shallow.setDeep(deep(i++));
        shallow.setDeeps(Arrays.asList(deep(i++), deep(i++)));
        return shallow;
    }

    public static Deep deep(int i) {
        Deep d = new Deep();
        d.setDeeper(deeper(i++));
        d.setDeepers(Arrays.asList(deeper(i++), deeper(i++)));
        return init(i++, d);
    }

    public static Deeper deeper(int i) {
        Deeper d = new Deeper();
        d.setDeepest(deepest(i++));
        d.setDeepests(Arrays.asList(deepest(i++), deepest(i++)));
        return init(i, d);
    }

    public static Deepest deepest(int i) {
        Deepest d = new Deepest();
        return init(i, d);
    }

    private static <T extends BaseObject> T init(int i, T base) {
        base.setId(id.incrementAndGet());
        base.setDepth(10);
        base.setName(String.valueOf((char) (i % 26 + 'A')));
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
