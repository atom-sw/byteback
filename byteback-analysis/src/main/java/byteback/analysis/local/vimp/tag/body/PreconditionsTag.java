package byteback.analysis.local.vimp.tag.body;

import soot.Value;

import java.util.List;

public class PreconditionsTag extends ConditionsTag {

    public static final String NAME = "PreconditionsTag";

    public PreconditionsTag(final List<Value> values) {
        super(values);
    }

    @Override
    public String getName() {
        return NAME;
    }

}
