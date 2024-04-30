package byteback.syntax.scene.type.declaration.member.method.body.tag;

import byteback.syntax.scene.type.declaration.member.method.body.value.ThrownLocal;
import soot.tagkit.AttributeValueException;
import soot.tagkit.Tag;

public class ThrownLocalTag implements Tag {

    public static final String NAME = "ThrownLocalTag";

    private final ThrownLocal thrownLocal;

    public ThrownLocalTag(final ThrownLocal thrownLocal) {
        this.thrownLocal = thrownLocal;
    }

    public ThrownLocal getThrownLocal() {
        return thrownLocal;
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
