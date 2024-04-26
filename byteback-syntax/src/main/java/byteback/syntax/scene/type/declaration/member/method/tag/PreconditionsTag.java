package byteback.syntax.scene.type.declaration.member.method.tag;

import soot.Value;

import java.util.ArrayList;
import java.util.List;

public class PreconditionsTag extends ConditionsTag {

    public static final String NAME = "PreconditionsTag";

    public PreconditionsTag(final List<Value> conditions) {
        super(conditions);
    }

    public PreconditionsTag() {
        this(new ArrayList<>());
    }

    @Override
    public String getName() {
        return NAME;
    }

}
