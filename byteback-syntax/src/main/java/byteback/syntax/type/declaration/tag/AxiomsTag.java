package byteback.syntax.type.declaration.tag;

import byteback.syntax.tag.ValuesTag;
import soot.Value;

import java.util.List;

public class AxiomsTag extends ValuesTag<Value> {

    public AxiomsTag(final List<Value> values) {
        super(values);
    }

    public static String NAME = "AxiomsTag";

    @Override
    public String getName() {
        return NAME;
    }

}
