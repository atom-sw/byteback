package byteback.syntax.scene.type.declaration.member.method.body.tag;

import byteback.syntax.scene.type.declaration.member.method.body.value.ArgumentLocal;
import soot.tagkit.AttributeValueException;
import soot.tagkit.Tag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ArgumentLocalsTag implements Tag {

    public static final String NAME = "ArgumentLocalsTag";

    private final List<ArgumentLocal> argumentLocals;

    public ArgumentLocalsTag(final List<ArgumentLocal> argumentLocals) {
        this.argumentLocals = argumentLocals;
    }

    public ArgumentLocalsTag() {
        this(new ArrayList<>());
    }

    public List<ArgumentLocal> getArgumentLocals() {
        return Collections.unmodifiableList(argumentLocals);
    }

    public void addArgumentLocal(final ArgumentLocal argumentLocal) {
        argumentLocals.add(argumentLocal);
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
