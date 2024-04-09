package byteback.analysis.local.vimp.tag.body;

import soot.Value;

import java.util.List;

public class PostconditionsTag extends ConditionsTag {

    public static final String NAME = "PostconditionsTag";

    public PostconditionsTag(final List<Value> values) {
        super(values);
    }

    @Override
    public String getName() {
        return NAME;
    }

}