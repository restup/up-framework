package com.deep;

import java.util.List;
import javax.validation.constraints.NotNull;

public class Deep extends BaseObject {

    @NotNull
    private Deeper deeper;
    private List<Deeper> deepers;

    public Deeper getDeeper() {
        return deeper;
    }

    public void setDeeper(Deeper deeper) {
        this.deeper = deeper;
    }

    public List<Deeper> getDeepers() {
        return deepers;
    }

    public void setDeepers(List<Deeper> deepers) {
        this.deepers = deepers;
    }

}
