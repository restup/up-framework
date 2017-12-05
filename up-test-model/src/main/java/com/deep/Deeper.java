package com.deep;

import java.util.List;
import javax.validation.constraints.NotNull;

public class Deeper extends BaseObject {

    @NotNull
    private Deepest deepest;
    private List<Deepest> deepests;

    public Deepest getDeepest() {
        return deepest;
    }

    public void setDeepest(Deepest deepest) {
        this.deepest = deepest;
    }

    public List<Deepest> getDeepests() {
        return deepests;
    }

    public void setDeepests(List<Deepest> deepests) {
        this.deepests = deepests;
    }

}
