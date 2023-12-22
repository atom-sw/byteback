package byteback.analysis.transformer;

import byteback.analysis.JimpleStmtSwitch;
import byteback.analysis.Namespace;
import byteback.analysis.Vimp;
import byteback.analysis.vimp.LogicStmt;
import byteback.util.Lazy;
import java.util.Map;
import soot.Body;
import soot.BodyTransformer;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.UnitBox;
import soot.Value;
import soot.grimp.GrimpBody;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;

public class LogicUnitTransformer extends BodyTransformer implements UnitTransformer {

	private static final Lazy<LogicUnitTransformer> instance = Lazy.from(LogicUnitTransformer::new);

	public static LogicUnitTransformer v() {
		return instance.get();
	}

	private LogicUnitTransformer() {
	}

	@Override
	public void internalTransform(final Body body, final String phaseName, final Map<String, String> options) {
		if (body instanceof GrimpBody) {
			transformBody(body);
		} else {
			throw new IllegalArgumentException("Can only transform Grimp");
		}
	}

	@Override
	public void transformUnit(final UnitBox unitBox) {
		final Unit unit = unitBox.getUnit();

		unit.apply(new JimpleStmtSwitch<>() {

			@Override
			public void caseInvokeStmt(final InvokeStmt invokeUnit) {
				final InvokeExpr value = invokeUnit.getInvokeExpr();
				final SootMethod method = value.getMethod();
				final SootClass declaringClass = method.getDeclaringClass();

				if (Namespace.isContractClass(declaringClass)) {
					assert value.getArgCount() == 1;
					final Value argument = value.getArg(0);

					final LogicStmt newUnit = switch (method.getName()) {
						case Namespace.ASSERTION_NAME -> Vimp.v().newAssertionStmt(argument);
						case Namespace.ASSUMPTION_NAME -> Vimp.v().newAssumptionStmt(argument);
						case Namespace.INVARIANT_NAME -> Vimp.v().newInvariantStmt(argument);
						default -> throw new IllegalStateException("Unknown logic statement " + method.getName());
					};

					newUnit.addAllTagsOf(invokeUnit);
					unitBox.setUnit(newUnit);
				}
			}

		});
	}

}
