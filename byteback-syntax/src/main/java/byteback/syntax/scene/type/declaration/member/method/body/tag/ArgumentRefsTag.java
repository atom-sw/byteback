package byteback.syntax.scene.type.declaration.member.method.body.tag;

import byteback.syntax.scene.type.declaration.member.method.body.value.ArgumentRef;
import soot.tagkit.AttributeValueException;
import soot.tagkit.Tag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ArgumentRefsTag implements Tag {

    public static final String NAME = "ArgumentRefsTag";

    private final List<ArgumentRef> argumentRefs;

    public ArgumentRefsTag(final List<ArgumentRef> argumentRefs) {
        this.argumentRefs = argumentRefs;
    }

    public ArgumentRefsTag() {
        this(new ArrayList<>());
    }

    public List<ArgumentRef> getArgumentRefs() {
        return Collections.unmodifiableList(argumentRefs);
    }

    public void addArgumentRef(final ArgumentRef argumentRef) {
        argumentRefs.add(argumentRef);
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
