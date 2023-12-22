package byteback.analysis.transformer;

import byteback.analysis.Namespace;
import byteback.analysis.util.SootHosts;
import byteback.util.Lazy;
import java.util.Iterator;
import java.util.Map;
import soot.Body;
import soot.BodyTransformer;
import soot.SootMethod;
import soot.Unit;
import soot.grimp.GrimpBody;
import soot.jimple.InvokeStmt;

public class PureTransformer extends BodyTransformer {

	private static final Lazy<PureTransformer> instance = Lazy.from(PureTransformer::new);

	public static PureTransformer v() {
		return instance.get();
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
		final Iterator<Unit> unitsIterator = body.getUnits().snapshotIterator();

		while (unitsIterator.hasNext()) {
			final Unit unit = unitsIterator.next();

			if (unit instanceof InvokeStmt invokeStmt) {
				final SootMethod method = invokeStmt.getInvokeExpr().getMethod();

				if (SootHosts.hasAnnotation(method, Namespace.IGNORE_ANNOTATION)) {
					body.getUnits().remove(unit);
				}
			}
		}
	}

}
