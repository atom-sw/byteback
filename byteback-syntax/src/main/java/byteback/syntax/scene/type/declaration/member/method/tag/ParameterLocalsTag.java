package byteback.syntax.scene.type.declaration.member.method.tag;

import byteback.syntax.tag.ValuesTag;
import soot.Local;
import soot.tagkit.AttributeValueException;
import soot.tagkit.Tag;

import java.util.ArrayList;
import java.util.List;

public class ParameterLocalsTag implements Tag {

    public static final String NAME = "ParametersLocalsTag";

    final List<Local> parameterLocals;

    public ParameterLocalsTag(final List<Local> parameterLocals) {
        this.parameterLocals = parameterLocals;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public byte[] getValue() throws AttributeValueException {
        return new byte[0];
    }

}
