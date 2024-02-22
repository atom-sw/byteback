package byteback.analysis.body.vimp.transformer;

import byteback.analysis.body.vimp.Vimp;
import byteback.util.Lazy;
import java.util.Map;
import soot.Body;
import soot.Scene;
import soot.Value;
import soot.grimp.Grimp;
import soot.grimp.GrimpBody;
import soot.jimple.ArrayRef;
import soot.jimple.IntConstant;

public class IndexCheckTransformer extends CheckTransformer {

	private static final Lazy<IndexCheckTransformer> instance = Lazy.from(IndexCheckTransformer::new);

	public static IndexCheckTransformer v() {
		return instance.get();
	}

	private IndexCheckTransformer() {
		super(Scene.v().loadClassAndSupport("java.lang.IndexOutOfBoundsException"));
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
