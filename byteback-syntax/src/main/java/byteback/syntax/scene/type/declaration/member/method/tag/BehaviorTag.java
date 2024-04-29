package byteback.syntax.scene.type.declaration.member.method.tag;

import byteback.common.function.Lazy;
import soot.tagkit.AttributeValueException;
import soot.tagkit.Tag;

/**
 * Tag used to annotate a methods specifying behavior expressions.
 *
 * @author paganma
 */
public class BehaviorTag implements Tag {

    public static String NAME = "BehaviorTag";

    private static final Lazy<BehaviorTag> INSTANCE = Lazy.from(BehaviorTag::new);

    public static BehaviorTag v() {
        return INSTANCE.get();
    }

    private BehaviorTag() {
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
