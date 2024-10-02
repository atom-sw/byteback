package byteback.syntax.scene.type.declaration.member.method.body.transformer;

import byteback.common.function.Lazy;
import byteback.syntax.scene.type.declaration.member.method.body.Vimp;
import byteback.syntax.scene.type.declaration.member.method.body.value.ThrownRef;
import soot.Body;
import soot.Local;
import soot.PatchingChain;
import soot.RefType;
import soot.Unit;
import soot.ValueBox;
import soot.jimple.Jimple;
import soot.jimple.internal.IdentityRefBox;
import soot.util.Chain;

public class ThrownLocalInserter extends BodyTransformer {

	private static final Lazy<ThrownLocalInserter> INSTANCE = Lazy.from(ThrownLocalInserter::new);

	public static ThrownLocalInserter v() {
		return INSTANCE.get();
	}

	private ThrownLocalInserter() {
	}

	@Override
	public void transformBody(final Body body) {
		final PatchingChain<Unit> units = body.getUnits();
		final Chain<Local> locals = body.getLocals();
		final Local local = Jimple.v().newLocal("e#", RefType.v("java.lang.Throwable"));
		units.addFirst(Jimple.v().newIdentityStmt(local, Vimp.v().newThrownRef()));
		locals.add(local);

		for (final ValueBox useBox : body.getUseBoxes()) {
			if (!(useBox instanceof IdentityRefBox)
					&& useBox.getValue() instanceof ThrownRef) {
				useBox.setValue(local);
			}
		}

		System.out.println(body);
	}

}
