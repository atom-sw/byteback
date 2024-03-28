package byteback.analysis.body.vimp.tag;

import byteback.common.function.Lazy;
import soot.tagkit.AttributeValueException;
import soot.tagkit.Tag;

/**
 * Tag used to annotate a behavioral method.
 *
 * @author paganma
 */
public class BehaviorBodyTag implements Tag {

    public static String NAME = "ExceptionalExitTag";

    private static final Lazy<BehaviorBodyTag> instance = Lazy.from(BehaviorBodyTag::new);

    public static BehaviorBodyTag v() {
        return instance.get();
    }

    private BehaviorBodyTag() {
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
