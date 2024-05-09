package byteback.syntax.scene.type.declaration.member.method.body.transformer;

import byteback.common.function.Lazy;
import byteback.syntax.scene.type.HeapType;
import byteback.syntax.scene.type.declaration.member.method.body.Vimp;
import byteback.syntax.scene.type.declaration.member.method.body.context.BodyContext;
import byteback.syntax.scene.type.declaration.member.method.tag.BehaviorTagMarker;
import soot.Body;
import soot.Local;
import soot.PatchingChain;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.Jimple;
import soot.util.Chain;

public class HeapLocalInserter extends BodyTransformer {

	private static final Lazy<HeapLocalInserter> INSTANCE = Lazy.from(HeapLocalInserter::new);

	public static HeapLocalInserter v() {
		return INSTANCE.get();
	}

	private HeapLocalInserter() {
	}

	@Override
	public void transformBody(final BodyContext bodyContext) {
		final SootMethod sootMethod = bodyContext.getSootMethod();

		if (!BehaviorTagMarker.v().hasTag(sootMethod)) {
			return;
		}

		final Body body = (Body) bodyContext.getBody();
		final PatchingChain<Unit> units = body.getUnits();
		final Chain<Local> locals = body.getLocals();
		final Local local = Jimple.v().newLocal("h", HeapType.v());
		units.addFirst(Jimple.v().newIdentityStmt(local, Vimp.v().newHeapRef()));
		locals.add(local);

		System.err.println(body);
	}

}
