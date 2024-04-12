package byteback.syntax.member.method.body.tag;

import byteback.common.function.Lazy;
import soot.tagkit.AttributeValueException;
import soot.tagkit.Tag;

/**
 * Tag used to annotate a methods specifying behavior expressions.
 *
 * @author paganma
 */
public class ExceptionalTag implements Tag {

    public static String NAME = "ExceptionalTag";

    private static final Lazy<ExceptionalTag> instance = Lazy.from(ExceptionalTag::new);

    public static ExceptionalTag v() {
        return instance.get();
    }

    private ExceptionalTag() {
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
