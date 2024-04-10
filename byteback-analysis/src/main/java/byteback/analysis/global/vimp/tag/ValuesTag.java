package byteback.analysis.global.vimp.tag;

import soot.ValueBox;
import soot.tagkit.AttributeValueException;
import soot.tagkit.Tag;

import java.util.List;

public abstract class ValuesTag<T extends ValueBox> implements Tag {

    private final List<T> valueBoxes;

    public ValuesTag(final List<T> valueBoxes) {
        this.valueBoxes = valueBoxes;
    }

    public List<T> getValueBoxes() {
        return valueBoxes;
    }

    @Override
    public byte[] getValue() throws AttributeValueException {
        return new byte[0];
    }

}
