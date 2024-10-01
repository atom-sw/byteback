package byteback.syntax.scene.type.declaration.member.method.transformer;

import java.util.HashSet;

import byteback.common.function.Lazy;
import byteback.syntax.scene.type.declaration.member.method.analysis.ParameterLocalFinder;
import byteback.syntax.scene.type.declaration.member.method.body.Vimp;
import byteback.syntax.scene.type.declaration.member.method.body.value.OldExpr;
import byteback.syntax.scene.type.declaration.member.method.tag.BehaviorTagMarker;
import byteback.syntax.scene.type.declaration.member.method.tag.NoStateTagMarker;
import byteback.syntax.scene.type.declaration.member.method.tag.TwoStateTagMarker;
import soot.*;

public class OldHeapReadTransformer extends MethodTransformer {

	private static final Lazy<OldHeapReadTransformer> INSTANCE = Lazy.from(OldHeapReadTransformer::new);

	public static OldHeapReadTransformer v() {
		return INSTANCE.get();
	}

	private OldHeapReadTransformer() {
	}

	@Override
	public void transformMethod(final SootMethod sootMethod) {
		final var useBoxes = new HashSet<ValueBox>();
		final Value heap;
		final Value oldHeap;

		if (BehaviorTagMarker.v().hasTag(sootMethod)) {
			if (!NoStateTagMarker.v().hasTag(sootMethod)) {
				heap = ParameterLocalFinder.v().findHeapLocal(sootMethod);
			} else {
				return;
			}

			if (TwoStateTagMarker.v().hasTag(sootMethod)) {
				oldHeap = ParameterLocalFinder.v().findOldHeapLocal(sootMethod);
			} else {
				oldHeap = null;
			}
		} else {
			return;
		}

		if (sootMethod.hasActiveBody()) {
			useBoxes.addAll(sootMethod.getActiveBody().getUseBoxes());
		}

		for (final ValueBox useBox : useBoxes) {
			if (useBox.getValue() instanceof final OldExpr oldExpr) {
				final Value oldOp = oldExpr.getOp();

				for (final ValueBox oldUseBox : oldOp.getUseBoxes()) {
					if (oldUseBox.getValue().equivTo(heap)) {
						if (oldHeap != null) {
							oldUseBox.setValue(Vimp.v().nest(oldHeap));
						} else {
							throw new IllegalStateException(
									"Attempting to reference pre-state from a non two-state behavior method.");
						}
					}
				}

				useBox.setValue(oldOp);
			}
		}
	}

}
