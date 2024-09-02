package byteback.syntax.scene.type.declaration.member.method.analysis;

import byteback.common.function.Lazy;
import byteback.syntax.scene.type.HeapType;
import byteback.syntax.scene.type.declaration.member.method.body.value.HeapRef;
import byteback.syntax.scene.type.declaration.member.method.body.value.OldHeapRef;
import byteback.syntax.scene.type.declaration.member.method.body.value.ThrownRef;
import soot.*;
import soot.jimple.IdentityRef;
import soot.jimple.Jimple;
import soot.jimple.ParameterRef;
import soot.jimple.ThisRef;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ParameterLocalFinder {

	private static final Lazy<ParameterLocalFinder> INSTANCE = Lazy.from(ParameterLocalFinder::new);

	public static ParameterLocalFinder v() {
		return INSTANCE.get();
	}

	private ParameterLocalFinder() {
	}

	public <T extends IdentityRef> Local findIdentityLocal(final Body body, final Class<T> identityRefType) {
		for (final Unit unit : body.getUnits()) {
			if (unit instanceof final IdentityUnit identityUnit) {
				if (identityRefType.isInstance(identityUnit.getRightOp())) {
					return (Local) identityUnit.getLeftOp();
				}
			}
		}

		return null;
	}

	public Local findThisLocal(final Body body) {
		return findIdentityLocal(body, ThisRef.class);
	}

	public Local findThisLocal(final SootMethod sootMethod) {
		if (sootMethod.hasActiveBody()) {
			return findThisLocal(sootMethod.getActiveBody());
		} else {
			return Jimple.v().newLocal("t#", sootMethod.getDeclaringClass().getType());
		}
	}

	public Local findParameterLocal(final SootMethod sootMethod, final int index) {
		if (sootMethod.hasActiveBody()) {
			final Body body = sootMethod.getActiveBody();
			return body.getParameterLocal(index);
		} else {
			return Jimple.v().newLocal("p" + index, sootMethod.getParameterType(index));
		}
	}

	public Local findHeapLocal(final Body body) {
		return findIdentityLocal(body, HeapRef.class);
	}

	public Local findHeapLocal(final SootMethod sootMethod) {
		if (sootMethod.hasActiveBody()) {
			return findHeapLocal(sootMethod.getActiveBody());
		} else {
			return Jimple.v().newLocal("h#", HeapType.v());
		}
	}

	public Local findOldHeapLocal(final Body body) {
		return findIdentityLocal(body, OldHeapRef.class);
	}

	public Local findOldHeapLocal(final SootMethod sootMethod) {
		if (sootMethod.hasActiveBody()) {
			return findOldHeapLocal(sootMethod.getActiveBody());
		} else {
			return Jimple.v().newLocal("h'#", HeapType.v());
		}
	}

	public Local findThrownLocal(final Body body) {
		return findIdentityLocal(body, ThrownRef.class);
	}

	public Local findThrownLocal(final SootMethod sootMethod) {
		if (sootMethod.hasActiveBody()) {
			return findThrownLocal(sootMethod.getActiveBody());
		} else {
			return Jimple.v().newLocal("e#", RefType.v("java.lang.Throwable"));
		}
	}

	public List<Local> findInputLocals(final SootMethod sootMethod) {
		final var parameterLocals = new ArrayList<Local>();
		final List<IdentityRef> inputRefs = ParameterRefFinder.v().findInputRefs(sootMethod);

		for (final IdentityRef inputRef : inputRefs) {
			if (inputRef instanceof ThisRef) {
				parameterLocals.add(findThisLocal(sootMethod));
			} else if (inputRef instanceof final ParameterRef parameterRef) {
				parameterLocals.add(findParameterLocal(sootMethod, parameterRef.getIndex()));
			} else if (inputRef instanceof HeapRef) {
				parameterLocals.add(findHeapLocal(sootMethod));
			} else if (inputRef instanceof OldHeapRef) {
				parameterLocals.add(findOldHeapLocal(sootMethod));
			} else if (inputRef instanceof ThrownRef) {
				parameterLocals.add(findThrownLocal(sootMethod));
			} else {
				throw new IllegalStateException("Unable to resolve input reference " + inputRef + " to local.");
			}
		}

		return Collections.unmodifiableList(parameterLocals);
	}

}
