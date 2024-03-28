package byteback.analysis.body.vimp.tag;

import byteback.common.function.Lazy;
import soot.tagkit.AttributeValueException;
import soot.tagkit.Tag;

/**
 * Tag used to annotate a behavioral method.
 *
 * @author paganma
 */
public class BehaviorMethodTag implements Tag {

    public static String NAME = "ExceptionalExitTag";

    private static final Lazy<BehaviorMethodTag> instance = Lazy.from(BehaviorMethodTag::new);

    public static BehaviorMethodTag v() {
        return instance.get();
    }

    private BehaviorMethodTag() {
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
