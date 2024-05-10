package byteback.syntax.scene.type.declaration.member.method.tag;

import byteback.common.function.Lazy;
import soot.tagkit.AttributeValueException;
import soot.tagkit.Tag;

/**
 * Tag marking methods accessing the two-state.
 *
 * @author paganma
 */
public class PureTag implements Tag {

    public static String NAME = "PureTag";

    private static final Lazy<PureTag> instance = Lazy.from(PureTag::new);

    public static PureTag v() {
        return instance.get();
    }

    private PureTag() {
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
