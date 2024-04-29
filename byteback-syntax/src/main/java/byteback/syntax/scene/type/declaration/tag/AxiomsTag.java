package byteback.syntax.scene.type.declaration.tag;

import soot.Value;
import soot.tagkit.AttributeValueException;
import soot.tagkit.Tag;

import java.util.ArrayList;
import java.util.List;

public class AxiomsTag implements Tag {

    public static String NAME = "AxiomsTag";

    final List<Value> axioms;

    public AxiomsTag(final List<Value> axioms) {
        this.axioms = axioms;
    }

    public AxiomsTag() {
        this(new ArrayList<>());
    }

    @Override
    public String getName() {
        return NAME;
    }

    public List<Value> getAxioms() {
        return axioms;
    }

    @Override
    public byte[] getValue() throws AttributeValueException {
        return new byte[0];
    }

}
