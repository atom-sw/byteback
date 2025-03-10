package byteback.syntax.scene.type.declaration.member.method.transformer;

import java.util.ArrayList;
import java.util.HashSet;

import byteback.common.function.Lazy;
import byteback.syntax.scene.type.BoxType;
import byteback.syntax.scene.type.PointerType;
import byteback.syntax.scene.type.declaration.member.method.analysis.ParameterLocalFinder;
import byteback.syntax.scene.type.declaration.member.method.body.Vimp;
import byteback.syntax.scene.type.declaration.member.method.body.value.CallExpr;
import byteback.syntax.scene.type.declaration.member.method.body.value.Pointer;
import byteback.syntax.scene.type.declaration.member.method.reference.InstanceOfRef;
import byteback.syntax.scene.type.declaration.member.method.reference.ReadRef;
import byteback.syntax.scene.type.declaration.member.method.reference.TypeToObjectRef;
import byteback.syntax.scene.type.declaration.member.method.reference.UnboxRef;
import byteback.syntax.scene.type.declaration.member.method.body.value.TypeConstant;
import byteback.syntax.scene.type.declaration.member.method.body.value.box.tag.HeapReaderTagMarker;
import byteback.syntax.scene.type.declaration.member.method.tag.BehaviorTagMarker;
import byteback.syntax.scene.type.declaration.member.method.tag.ExceptionalTagMarker;
import byteback.syntax.scene.type.declaration.member.method.tag.NoStateTagMarker;
import byteback.syntax.scene.type.declaration.member.method.tag.PostassumptionsTagAccessor;
import byteback.syntax.scene.type.declaration.member.method.tag.PostconditionsTagAccessor;
import byteback.syntax.scene.type.declaration.member.method.tag.PreassumptionsTagAccessor;
import byteback.syntax.scene.type.declaration.member.method.tag.PreconditionsTagAccessor;
import byteback.syntax.scene.type.declaration.member.method.tag.TwoStateTagMarker;
import byteback.syntax.scene.type.declaration.member.method.body.unit.CallStmt;
import soot.*;
import soot.jimple.ArrayRef;
import soot.jimple.ConcreteRef;
import soot.jimple.FieldRef;
import soot.jimple.IdentityRef;
import soot.jimple.InstanceFieldRef;
import soot.jimple.InstanceOfExpr;
import soot.jimple.StaticFieldRef;

public class HeapReadTransformer extends MethodTransformer {

	private static final Lazy<HeapReadTransformer> INSTANCE = Lazy.from(HeapReadTransformer::new);

	public static HeapReadTransformer v() {
		return INSTANCE.get();
	}

	private HeapReadTransformer() {
	}

	@Override
	public void transformMethod(final SootMethod sootMethod) {
		final Value heap;
		final Value oldHeap;
		final Value thrown;
		final var valueBoxes = new HashSet<ValueBox>();

		if (BehaviorTagMarker.v().hasTag(sootMethod)) {
			heap = ParameterLocalFinder.v().findHeapLocal(sootMethod);

			if (TwoStateTagMarker.v().hasTag(sootMethod)) {
				oldHeap = ParameterLocalFinder.v().findOldHeapLocal(sootMethod);
			} else {
				oldHeap = null;
			}

			if (ExceptionalTagMarker.v().hasTag(sootMethod)) {
				thrown = ParameterLocalFinder.v().findThrownLocal(sootMethod);
			} else {
				thrown = null;
			}
		} else {
			heap = Vimp.v().newHeapRef();
			oldHeap = Vimp.v().newOldExpr(Vimp.v().nest(heap));
			thrown = Vimp.v().newThrownRef();

			PreconditionsTagAccessor.v().get(sootMethod).ifPresent((preconditionsTag) -> {
				valueBoxes.addAll(preconditionsTag.getUseBoxes());
			});

			PostconditionsTagAccessor.v().get(sootMethod).ifPresent((postconditionsTag) -> {
				valueBoxes.addAll(postconditionsTag.getUseBoxes());
			});

			PreassumptionsTagAccessor.v().get(sootMethod).ifPresent((preconditionsTag) -> {
				valueBoxes.addAll(preconditionsTag.getUseBoxes());
			});

			PostassumptionsTagAccessor.v().get(sootMethod).ifPresent((postconditionsTag) -> {
				valueBoxes.addAll(postconditionsTag.getUseBoxes());
			});
		}

		if (sootMethod.hasActiveBody()) {
			final Body body = sootMethod.getActiveBody();

			for (final Unit unit : body.getUnits()) {
				if (unit instanceof CallStmt callStmt) {
					valueBoxes.addAll(callStmt.getInvokeExpr().getUseBoxes());
				} else {
					valueBoxes.addAll(unit.getUseBoxes());
				}
			}
		}

		// Convert heap uses
		for (final ValueBox valueBox : valueBoxes) {
			if (HeapReaderTagMarker.v().hasTag(valueBox)) {
				continue;
			}

			final Value value = valueBox.getValue();

			if (value instanceof final CallExpr callExpr) {
				final SootMethod calledMethod = callExpr.getMethod();
				int index = 0;

				final var arguments = new ArrayList<>(callExpr.getArgs());

				if (!NoStateTagMarker.v().hasTag(calledMethod)) {
					arguments.add(index++, Vimp.v().nest(heap));
				}

				if (TwoStateTagMarker.v().hasTag(calledMethod)) {
					if (oldHeap != null) {
						arguments.add(index++, Vimp.v().nest(oldHeap));
					} else {
						throw new IllegalStateException(
								"Only a two-state behavior method can call another two-state behavior method.");
					}
				}

				if (ExceptionalTagMarker.v().hasTag(calledMethod)) {
					if (thrown != null) {
						arguments.add(index++, Vimp.v().nest(thrown));
					} else {
						throw new IllegalStateException(
								"Only an exceptional behavior method can call another exceptional behavior method.");
					}
				}

				final CallExpr newCallExpr = Vimp.v().newCallExpr(callExpr.getMethodRef(), arguments);
				valueBox.setValue(newCallExpr);
			} else if (value instanceof final InstanceOfExpr instanceOfExpr) {
				valueBox.setValue(Vimp.v().newCallExpr(InstanceOfRef.v(),
						Vimp.v().nest(heap), Vimp.v().nest(instanceOfExpr.getOp()),
						Vimp.v().newTypeConstant((RefLikeType) instanceOfExpr.getCheckType())));
			} else if (value instanceof final ConcreteRef concreteRef
					&& !(concreteRef instanceof IdentityRef)) {
				final Value base;
				final Pointer pointer;

				if (value instanceof final FieldRef fieldRef) {
					if (fieldRef instanceof final InstanceFieldRef instanceFieldRef) {
						base = instanceFieldRef.getBase();
					} else if (fieldRef instanceof final StaticFieldRef staticFieldRef) {
						final RefType declaringClassType = staticFieldRef.getField().getDeclaringClass().getType();
						final TypeConstant typeConstant = Vimp.v().newTypeConstant(declaringClassType);
						base = Vimp.v().newCallExpr(TypeToObjectRef.v(), typeConstant);
					} else {
						throw new IllegalStateException("Unable to convert field reference " + fieldRef + ".");
					}

					pointer = Vimp.v().newFieldPointer(fieldRef.getFieldRef());
					final var readRef = new ReadRef(pointer.getType());
					valueBox.setValue(
							Vimp.v().newCallExpr(readRef,
									Vimp.v().nest(heap), Vimp.v().nest(base), Vimp.v().nest(pointer)));
				} else if (value instanceof final ArrayRef arrayRef) {
					base = arrayRef.getBase();
					pointer = Vimp.v().newArrayPointer(arrayRef.getType(), arrayRef.getIndex());
					final var readRef = new ReadRef(new PointerType(new BoxType(pointer.getType())));
					final var unboxRef = new UnboxRef(pointer.getType());
					valueBox.setValue(
							Vimp.v().newCallExpr(unboxRef,
									Vimp.v().nest(
											Vimp.v().newCallExpr(readRef,
													Vimp.v().nest(heap), Vimp.v().nest(base), Vimp.v().nest(pointer)))));
				} else {
					throw new IllegalStateException("Unable to convert reference " + concreteRef + " to pointer.");
				}
			}

			HeapReaderTagMarker.v().flag(valueBox);
		}
	}

}
