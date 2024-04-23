package byteback.syntax.scene.type.declaration.tag;

import byteback.syntax.tag.ValuesTag;
import soot.Value;

import java.util.ArrayList;
import java.util.List;

public class AxiomsTag extends ValuesTag<Value> {

    public static String NAME = "AxiomsTag";

    public AxiomsTag(final List<Value> values) {
        super(values);
    }

    public AxiomsTag() {
        this(new ArrayList<>());
    }

    @Override
    public String getName() {
        return NAME;
    }

}
