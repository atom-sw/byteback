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
import java.util.List;

public class ParameterLocalFinder {

	private static final Lazy<ParameterLocalFinder> INSTANCE = Lazy.from(ParameterLocalFinder::new);

	public static ParameterLocalFinder v() {
		return INSTANCE.get();
	}

	private ParameterLocalFinder() {
	}

	public Local findHeapLocal(final Body body) {
		for (final Unit unit : body.getUnits()) {
			if (unit instanceof final IdentityUnit identityUnit) {
				if (identityUnit.getRightOp() instanceof HeapRef) {
					return (Local) identityUnit.getLeftOp();
				}
			}
		}

		throw new IllegalArgumentException("Body does not assigns heap local.");
	}

	public Local findHeapLocal(final SootMethod sootMethod) {
		if (sootMethod.hasActiveBody()) {
			return findHeapLocal(sootMethod.getActiveBody());
		} else {
			return Jimple.v().newLocal("h#", HeapType.v());
		}
	}

	public Local findOldHeapLocal(final Body body) {
		for (final Unit unit : body.getUnits()) {
			if (unit instanceof final IdentityUnit identityUnit) {
				if (identityUnit.getRightOp() instanceof OldHeapRef) {
					return (Local) identityUnit.getLeftOp();
				}
			}
		}

		throw new IllegalArgumentException("Body does not assigns old heap local.");
	}

	public Local findOldHeapLocal(final SootMethod sootMethod) {
		if (sootMethod.hasActiveBody()) {
			return findOldHeapLocal(sootMethod.getActiveBody());
		} else {
			return Jimple.v().newLocal("h'#", HeapType.v());
		}
	}

	public Local findThrownLocal(final Body body) {
		for (final Unit unit : body.getUnits()) {
			if (unit instanceof final IdentityUnit identityUnit) {
				if (identityUnit.getRightOp() instanceof ThrownRef) {
					return (Local) identityUnit.getLeftOp();
				}
			}
		}

		throw new IllegalArgumentException("Body does not assigns thrown local.");
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
		final Body body = sootMethod.hasActiveBody() ? sootMethod.getActiveBody() : null;

		for (final IdentityRef inputRef : inputRefs) {
			if (inputRef instanceof final ThisRef thisRef) {
				if (body != null) {
					final Local thisLocal = body.getThisLocal();
					assert thisLocal.getType() == thisRef.getType();
					parameterLocals.add(thisLocal);
				} else {
					parameterLocals.add(Jimple.v().newLocal("t", thisRef.getType()));
				}
			} else if (inputRef instanceof final ParameterRef parameterRef) {
				if (body != null) {
					final Local parameterLocal = body.getParameterLocal(parameterRef.getIndex());
					assert parameterLocal.getType() == parameterRef.getType();
					parameterLocals.add(parameterLocal);
				} else {
					parameterLocals.add(Jimple.v().newLocal("p" + parameterRef.getIndex(), parameterRef.getType()));
				}
			} else if (inputRef instanceof HeapRef) {
				if (body != null) {
					parameterLocals.add(findHeapLocal(body));
				} else {
					parameterLocals.add(findHeapLocal(sootMethod));
				}
			} else if (inputRef instanceof OldHeapRef) {
				if (body != null) {
					parameterLocals.add(findOldHeapLocal(body));
				} else {
					parameterLocals.add(findOldHeapLocal(sootMethod));
				}
			} else if (inputRef instanceof ThrownRef) {
				if (body != null) {
					parameterLocals.add(findThrownLocal(body));
				} else {
					parameterLocals.add(findThrownLocal(sootMethod));
				}
			} else {
				throw new IllegalStateException("Unable to resolve input reference " + inputRef + " to local.");
			}
		}

		return parameterLocals;
	}

}
