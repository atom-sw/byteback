package byteback.syntax.scene.type.declaration.member.method.body.tag;

import byteback.syntax.scene.type.declaration.member.method.body.value.ThisLocal;
import soot.tagkit.AttributeValueException;
import soot.tagkit.Tag;

public class ThisLocalTag implements Tag {

    public static final String NAME = "ThisLocalTag";

    private final ThisLocal thisLocal;

    public ThisLocalTag(final ThisLocal thisLocal) {
        this.thisLocal = thisLocal;
    }

    public ThisLocal getThisLocal() {
        return thisLocal;
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
