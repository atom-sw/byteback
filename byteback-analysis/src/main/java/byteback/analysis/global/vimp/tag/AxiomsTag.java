package byteback.analysis.global.vimp.tag;

import byteback.analysis.local.vimp.syntax.value.box.ConditionExprBox;

import java.util.List;

public class AxiomsTag extends ValuesTag<ConditionExprBox> {

    public static String NAME = "AxiomsTag";

    public AxiomsTag(final List<ConditionExprBox> valueBoxes) {
        super(valueBoxes);
    }

    @Override
    public String getName() {
        return NAME;
    }

}
