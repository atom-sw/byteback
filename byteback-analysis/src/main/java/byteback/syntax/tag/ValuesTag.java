package byteback.syntax.tag;

import soot.Value;
import soot.tagkit.Tag;

import java.util.ArrayList;
import java.util.List;

/**
 * Generic tag containing values.
 *
 * @param <T> The base type of the contained values.
 * @author paganma
 */
public abstract class ValuesTag<T extends Value> implements Tag {

    private final List<T> specificationValues;

    /**
     * Constructs a new ValuesTag.
     *
     * @param values The values associated to this tag.
     */
    public ValuesTag(final List<T> values) {
        this.specificationValues = values;
    }

    /**
     * Constructs a new empty ValuesTag.
     */
    public ValuesTag() {
        this(new ArrayList<>());
    }

    /**
     * Getter for the specification values contained in this tag.
     *
     * @return The specification values contained in this tag.
     */
    public List<T> getValues() {
        return specificationValues;
    }

    /**
     * This method does not refer to the values contained within the tags.
     *
     * @return The value in bytes for this tag.
     */
    @Override
    public byte[] getValue() {
        return new byte[0];
    }

}
