package byteback.syntax.scene.type.declaration.member.method.body.tag;

import byteback.syntax.scene.type.declaration.member.method.body.value.InstanceRef;
import soot.tagkit.AttributeValueException;
import soot.tagkit.Tag;

public class InstanceRefTag implements Tag {

    public static final String NAME = "InstanceRefTag";

    private final InstanceRef instanceRef;

    public InstanceRefTag(final InstanceRef instanceRef) {
        this.instanceRef = instanceRef;
    }

    public InstanceRef getInstanceRef() {
        return instanceRef;
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
