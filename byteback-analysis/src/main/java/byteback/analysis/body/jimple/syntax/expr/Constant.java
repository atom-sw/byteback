package byteback.analysis.body.jimple.syntax.expr;

import java.util.Collections;
import java.util.List;

import byteback.analysis.body.common.syntax.Immediate;
import byteback.analysis.body.common.syntax.Value;
import byteback.analysis.body.common.syntax.ValueBox;

public abstract class Constant implements Value, Immediate {

    @Override
    public final List<ValueBox> getUseBoxes() {
        return Collections.emptyList();
    }

    /**
     * Returns a hash code consistent with structural equality for this object. For Constants, equality is structural equality;
     * we hope that each subclass defines hashCode() correctly.
     */
    @Override
    public int equivHashCode() {
        return hashCode();
    }
}