package byteback.analysis.local.vimp.tag.body;

import soot.Value;

import java.util.List;

public class PostconditionTag extends ConditionsTag {

    public static final String NAME = "PreconditionsTag";

    public PostconditionTag(final List<Value> values) {
        super(values);
    }

    @Override
    public String getName() {
        return NAME;
    }

}
