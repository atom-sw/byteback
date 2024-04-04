package byteback.analysis.local.vimp.tag.body;

import byteback.common.function.Lazy;
import soot.tagkit.AttributeValueException;
import soot.tagkit.Tag;

/**
 * Tag used to annotate a methods specifying behavior expressions.
 *
 * @author paganma
 */
public class BehaviorTag implements Tag {

    public static String NAME = "ExceptionalExitTag";

    private static final Lazy<BehaviorTag> instance = Lazy.from(BehaviorTag::new);

    public static BehaviorTag v() {
        return instance.get();
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
