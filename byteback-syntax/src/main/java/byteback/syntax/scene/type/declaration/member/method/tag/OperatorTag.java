package byteback.syntax.scene.type.declaration.member.method.tag;

import byteback.common.function.Lazy;
import soot.tagkit.AttributeValueException;
import soot.tagkit.Tag;

public class OperatorTag implements Tag {

    public static final String NAME = "OperatorTag";

    private static final Lazy<OperatorTag> INSTANCE = Lazy.from(OperatorTag::new);

    public static OperatorTag v() {
        return INSTANCE.get();
    }

    private OperatorTag() {
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