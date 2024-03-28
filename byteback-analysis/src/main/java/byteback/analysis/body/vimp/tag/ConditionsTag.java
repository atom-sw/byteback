package byteback.analysis.body.vimp.tag;

import byteback.analysis.body.vimp.syntax.LogicExpr;

import java.util.List;

public abstract class ConditionsTag extends ValuesTag<LogicExpr> {

    public ConditionsTag(final List<LogicExpr> values) {
        super(values);
    }

}
