package byteback.syntax.scene.type.declaration.member.method.tag;

import soot.Value;

import java.util.ArrayList;
import java.util.List;

public class PostconditionsTag extends ConditionsTag {

    public static final String NAME = "PostconditionsTag";

    public PostconditionsTag(final List<Value> values) {
        super(values);
    }

    public PostconditionsTag() {
        this(new ArrayList<>());
    }

    @Override
    public String getName() {
        return NAME;
    }

}