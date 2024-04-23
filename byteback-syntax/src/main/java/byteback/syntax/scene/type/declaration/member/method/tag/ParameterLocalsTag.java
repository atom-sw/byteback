package byteback.syntax.scene.type.declaration.member.method.tag;

import byteback.syntax.tag.ValuesTag;
import soot.Local;

import java.util.ArrayList;
import java.util.List;

public class ParameterLocalsTag extends ValuesTag<Local> {

    public static final String NAME = "ParametersLocalsTag";

    public ParameterLocalsTag(final List<Local> parameterLocals) {
        super(parameterLocals);
    }

    public ParameterLocalsTag() {
        this(new ArrayList<>());
    }

    @Override
    public String getName() {
        return NAME;
    }

}
