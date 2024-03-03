package byteback.analysis.scene.tag;

import soot.tagkit.AttributeValueException;
import soot.tagkit.Tag;

public class PurityTag implements Tag {

    private final boolean pureFlag;

    public PurityTag(final boolean pureFlag) {
        this.pureFlag = pureFlag;
    }

    @Override
    public String getName() {
        return "PurityTag";
    }

    public boolean isPure() {
        return pureFlag;
    }

    @Override
    public byte[] getValue() throws AttributeValueException {
        return new byte[2];
    }

}
