package byteback.analysis.body.common.syntax.expr;

import java.util.Collections;
import java.util.List;

public abstract class Constant implements Value {

    @Override
    public final List<ValueBox> getUseBoxes() {
        return Collections.emptyList();
    }
}
