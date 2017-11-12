package com.deep;

import javax.validation.constraints.NotNull;
import java.util.List;

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
