package byteback.analysis.transformer;

import byteback.util.Lazy;
import java.util.Map;
import soot.Body;
import soot.BodyTransformer;
import soot.Value;
import soot.ValueBox;
import soot.grimp.Grimp;
import soot.grimp.GrimpBody;
import soot.jimple.DynamicInvokeExpr;

public class DynamicToStaticTransformer extends BodyTransformer {

	private static final Lazy<DynamicToStaticTransformer> instance = Lazy.from(DynamicToStaticTransformer::new);

	public static DynamicToStaticTransformer v() {
		return instance.get();
	}

	private DynamicToStaticTransformer() {
	}

	@Override
	public void internalTransform(final Body body, final String phaseName, final Map<String, String> options) {
		if (body instanceof GrimpBody) {
			transformBody(body);
		} else {
			throw new IllegalArgumentException("Can only transform Grimp");
		}
	}

	public void transformBody(final Body body) {
		for (final ValueBox vbox : body.getUseBoxes()) {
			final Value value = vbox.getValue();

			if (value instanceof DynamicInvokeExpr invokeDynamic) {
				vbox.setValue(Grimp.v().newStaticInvokeExpr(invokeDynamic.getMethodRef(), invokeDynamic.getArgs()));
			}
		}
	}

}
