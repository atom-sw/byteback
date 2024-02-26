package byteback.analysis.body.vimp.transformer;

import byteback.analysis.body.grimp.transformer.ExprFolder;
import byteback.common.Lazy;
import soot.Immediate;
import soot.Value;

public class VimpExprFolder extends ExprFolder {

    private static final Lazy<VimpExprFolder> instance = Lazy.from(VimpExprFolder::new);

    public static VimpExprFolder v() {
        return instance.get();
    }


    @Override
    public boolean canSubstitute(final Value substitution) {
        return substitution instanceof Immediate;
    }

}
