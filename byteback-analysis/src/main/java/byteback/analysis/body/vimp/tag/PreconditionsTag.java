package byteback.analysis.body.vimp.tag;

import byteback.analysis.body.vimp.syntax.LogicExpr;

import java.util.List;

public class PreconditionsTag extends ConditionsTag {

    public static final String NAME = "PreconditionsTag";

    public PreconditionsTag(final List<LogicExpr> values) {
        super(values);
    }

    @Override
    public String getName() {
        return NAME;
    }

}
