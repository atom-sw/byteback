package byteback.analysis.body.vimp.transformer;

import byteback.analysis.body.grimp.visitor.AbstractGrimpValueSwitchWithInvokeCase;
import byteback.analysis.common.namespace.BBLibNamespace;
import byteback.analysis.body.vimp.QuantifierExpr;
import byteback.analysis.body.vimp.Vimp;
import byteback.analysis.body.common.transformer.ValueTransformer;
import byteback.util.Lazy;
import java.util.Iterator;
import java.util.Map;
import soot.Body;
import soot.BodyTransformer;
import soot.Local;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.grimp.GrimpBody;
import soot.jimple.AssignStmt;
import soot.jimple.CastExpr;
import soot.jimple.InvokeExpr;
import soot.util.Chain;
import soot.util.HashChain;

public class QuantifierValueTransformer extends BodyTransformer implements ValueTransformer {

	private static final Lazy<QuantifierValueTransformer> instance = Lazy.from(QuantifierValueTransformer::new);

	public static QuantifierValueTransformer v() {
		return instance.get();
	}

	private QuantifierValueTransformer() {
	}

	@Override
	protected void internalTransform(final Body body, final String phaseName, final Map<String, String> options) {
		if (body instanceof GrimpBody) {
			transformBody(body);
		} else {
			throw new IllegalArgumentException("Can only transform Grimp");
		}
	}

	@Override
	public void transformBody(final Body body) {
		final Iterator<Unit> unitIterator = body.getUnits().snapshotIterator();

		while (unitIterator.hasNext()) {
			final Unit unit = unitIterator.next();

			if (unit instanceof AssignStmt assignStmt) {
				if (assignStmt.getRightOp()instanceof InvokeExpr invokeExpr) {
					final SootMethod method = invokeExpr.getMethod();
					final SootClass clazz = method.getDeclaringClass();

					if (BBLibNamespace.isBindingClass(clazz)) {
						body.getUnits().remove(assignStmt);
					}
				}
			} else {
				for (final ValueBox vbox : unit.getUseAndDefBoxes()) {
					transformValue(vbox);
				}
			}
		}
	}

	@Override
	public void transformValue(final ValueBox valueBox) {
		final Value value = valueBox.getValue();

		value.apply(new AbstractGrimpValueSwitchWithInvokeCase<>() {

			@Override
			public void caseInvokeExpr(final InvokeExpr value) {
				assert value.getArgCount() == 2;

				final SootMethod method = value.getMethod();
				final SootClass clazz = method.getDeclaringClass();

				if (BBLibNamespace.isQuantifierClass(clazz)) {
					final Chain<Local> locals = new HashChain<>();
					final Value expression;
					Value variable = value.getArg(0);

					while (variable instanceof CastExpr castExpr) {
						variable = castExpr.getOp();
					}

					if (variable instanceof Local local) {
						locals.add(local);
						expression = value.getArg(1);
					} else {
						throw new RuntimeException("First argument of quantifier must be a local variable");
					}

					final QuantifierExpr substitute = switch (method.getName()) {
						case BBLibNamespace.UNIVERSAL_QUANTIFIER_NAME ->
								Vimp.v().newLogicForallExpr(locals, expression);
						case BBLibNamespace.EXISTENTIAL_QUANTIFIER_NAME ->
								Vimp.v().newLogicExistsExpr(locals, expression);
						default ->
								throw new IllegalStateException("Unknown quantifier method " + method.getName());
					};

					valueBox.setValue(substitute);
				}

			}

		});
	}

}
