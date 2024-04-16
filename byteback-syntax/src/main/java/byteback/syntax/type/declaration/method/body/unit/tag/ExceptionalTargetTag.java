package byteback.syntax.type.declaration.method.body.unit.tag;

import byteback.common.function.Lazy;
import soot.tagkit.AttributeValueException;
import soot.tagkit.Tag;

/**
 * Tag used to annotate a branch associated with an exception handler.
 *
 * @author paganma
 */
public class ExceptionalTargetTag implements Tag {

    public static String NAME = "ExceptionalTargetTag";

    private static final Lazy<ExceptionalTargetTag> instance = Lazy.from(ExceptionalTargetTag::new);

    public static ExceptionalTargetTag v() {
        return instance.get();
    }

    private ExceptionalTargetTag() {
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
