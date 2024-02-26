package byteback.analysis.body.vimp.transformer;

import byteback.analysis.body.vimp.Vimp;
import byteback.common.Lazy;
import soot.Body;
import soot.PhaseOptions;
import soot.Scene;
import soot.Value;
import soot.grimp.Grimp;
import soot.jimple.ArrayRef;
import soot.jimple.IntConstant;
import soot.jimple.JimpleBody;

import java.util.Map;

public class IndexCheckTransformer extends CheckTransformer {

    private static final Lazy<IndexCheckTransformer> instance = Lazy.from(IndexCheckTransformer::new);

    private IndexCheckTransformer() {
        super(Scene.v().loadClassAndSupport("java.lang.IndexOutOfBoundsException"));
    }

    public static IndexCheckTransformer v() {
        return instance.get();
    }

    @Override
    public void internalTransform(final Body body, final String phaseName, final Map<String, String> options) {
        if (PhaseOptions.getBoolean(options, "enabled")) {
            transformBody(body);
        }
    }

    @Override
    public Value extractTarget(final Value value) {
        Value target = null;

        if (value instanceof ArrayRef arrayRef) {
            target = arrayRef.getBase();
        }

        return target;
    }

    @Override
    public Value makeCheckExpr(Value inner, Value outer) {
        if (outer instanceof ArrayRef arrayRef) {
            final Value indexValue = arrayRef.getIndex();
            final Value arrayBase = arrayRef.getBase();
            final Value lengthOfExpr = Grimp.v().newLengthExpr(arrayBase);
            final Value left = Vimp.v().newLtExpr(indexValue, lengthOfExpr);
            final Value right = Vimp.v().newLeExpr(IntConstant.v(0), indexValue);
            return Vimp.v().newLogicAndExpr(left, right);
        } else {
            throw new IllegalStateException();
        }
    }

}
