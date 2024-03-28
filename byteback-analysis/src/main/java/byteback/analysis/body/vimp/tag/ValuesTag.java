package byteback.analysis.body.vimp.tag;

import soot.Value;
import soot.tagkit.AttributeValueException;
import soot.tagkit.Tag;

import java.util.List;

public abstract class ValuesTag<T extends Value> implements Tag {

    private final List<T> values;

    public ValuesTag(final List<T> values) {
        this.values = values;
    }

    public List<T> getValues() {
        return values;
    }

    @Override
    public byte[] getValue() throws AttributeValueException {
        return new byte[0];
    }

}
