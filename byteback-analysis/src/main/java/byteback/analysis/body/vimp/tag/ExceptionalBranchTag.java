package byteback.analysis.body.vimp.tag;

import byteback.common.function.Lazy;
import soot.tagkit.AttributeValueException;
import soot.tagkit.Tag;

/**
 * Tag used to annotate a branch associated with an exception handler.
 * @author paganma
 */
public class ExceptionalBranchTag implements Tag {

    public static String NAME = "ExceptionalExitTag";

    private static final Lazy<ExceptionalBranchTag> instance = Lazy.from(ExceptionalBranchTag::new);

    public static ExceptionalBranchTag v() {
        return instance.get();
    }

    private ExceptionalBranchTag() {
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
