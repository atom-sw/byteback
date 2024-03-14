package byteback.analysis.body.jimple.syntax.expr;

import java.util.Collections;
import java.util.List;

import byteback.analysis.body.common.syntax.expr.Immediate;
import byteback.analysis.body.common.syntax.expr.Value;
import byteback.analysis.body.common.syntax.expr.ValueBox;

public abstract class Constant implements Value, Immediate {

    @Override
    public final List<ValueBox> getUseBoxes() {
        return Collections.emptyList();
    }
}