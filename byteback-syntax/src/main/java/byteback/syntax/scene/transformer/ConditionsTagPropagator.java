package byteback.syntax.scene.transformer;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import byteback.common.function.Lazy;
import byteback.syntax.scene.type.declaration.member.method.body.Vimp;
import byteback.syntax.scene.type.declaration.member.method.tag.OnlyPostconditionsTagAccessor;
import byteback.syntax.scene.type.declaration.member.method.tag.OnlyPreconditionsTagAccessor;
import byteback.syntax.scene.type.declaration.member.method.tag.PostconditionsTag;
import byteback.syntax.scene.type.declaration.member.method.tag.PostconditionsTagAccessor;
import byteback.syntax.scene.type.declaration.member.method.tag.PreconditionsTag;
import byteback.syntax.scene.type.declaration.member.method.tag.PreconditionsTagAccessor;
import soot.Hierarchy;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Value;
import soot.VoidType;
import soot.jimple.Jimple;
import soot.jimple.JimpleBody;
import soot.util.NumberedString;

/**
 * Propagates method implementations and specification through the
 * {@link byteback.specification.ghost.Ghost.Attach} annotation.
 *
 * @author paganma
 */
public class ConditionsTagPropagator extends SceneTransformer {

	private static final Lazy<ConditionsTagPropagator> INSTANCE = Lazy.from(ConditionsTagPropagator::new);

	public static ConditionsTagPropagator v() {
		return INSTANCE.get();
	}

	private ConditionsTagPropagator() {
	}

	public PreconditionsTag weakenPreconditions(
			final PreconditionsTag parentPreconditionsTag,
			final PreconditionsTag childPreconditionsTag) {
		final var weakenedPreconditionsTag = new PreconditionsTag();

		// For every parent precondition p_{0 ... m}, and every child
		// precondition c_{0 ... n}, the weakened preconditions will
		// be the disjunction of each child precondition c_j, with
		// each parent precondition p_i: c_j | p_0 | p_1 | ... | p_m
		for (final Value childPrecondition : childPreconditionsTag.getConditions()) {
			Value weakenedPrecondition = childPrecondition;

			for (final Value parentPrecondition : parentPreconditionsTag.getConditions()) {
				weakenedPrecondition = Jimple.v().newOrExpr(Vimp.v().nest(weakenedPrecondition),
						Vimp.v().nest(parentPrecondition));
			}

			weakenedPreconditionsTag.addCondition(weakenedPrecondition);
		}

		return weakenedPreconditionsTag;
	}

	public PostconditionsTag strenghtenPostconditions(
			final PostconditionsTag parentPostconditionsTag,
			final PostconditionsTag childPostconditionsTag) {
		final var strenghtenedPostconditions = new ArrayList<>(parentPostconditionsTag.getConditions());
		final var strenghtenedPostconditionsTag = new PostconditionsTag(strenghtenedPostconditions);

		for (final Value parentPostcondition : childPostconditionsTag.getConditions()) {
			strenghtenedPostconditionsTag.addCondition(parentPostcondition);
		}

		return strenghtenedPostconditionsTag;
	}

	@Override
	public void transformScene(final Scene scene) {
		final Hierarchy hierarchy = scene.getActiveHierarchy();
		final var nextClasses = new ArrayDeque<SootClass>();

		for (final SootClass sootClass : scene.getClasses()) {
			if (sootClass.resolvingLevel() < SootClass.HIERARCHY
					|| sootClass.getSuperclassUnsafe() != null
					|| sootClass.getInterfaceCount() > 0) {
				continue;
			}

			final var classToParentMethods = new HashMap<SootClass, HashMap<NumberedString, SootMethod>>();
			nextClasses.push(sootClass);

			while (!nextClasses.isEmpty()) {
				final SootClass currentClass = nextClasses.pop();
				final HashMap<NumberedString, SootMethod> parentMethods = classToParentMethods
						.computeIfAbsent(currentClass, ($) -> new HashMap<>());

				if (currentClass.resolvingLevel() >= SootClass.SIGNATURES) {
					for (final SootMethod currentMethod : new ArrayList<>(currentClass.getMethods())) {
						final NumberedString currentMethodSignature = currentMethod.getNumberedSubSignature();
						final SootMethod parentMethod = parentMethods.get(currentMethodSignature);
						final AtomicBoolean introducesSpecification = new AtomicBoolean(false);

						// Handling automatic specification inheritance
						PreconditionsTagAccessor.v().get(currentMethod).ifPresentOrElse((currentPreconditionsTag) -> {
							if (parentMethod != null) {
								PreconditionsTagAccessor.v().get(parentMethod).ifPresent((parentPreconditionsTag) -> {
									PreconditionsTagAccessor.v().put(
											currentMethod,
											weakenPreconditions(parentPreconditionsTag, currentPreconditionsTag));
								});
							}

							introducesSpecification.set(true);
						}, () -> {
							if (parentMethod != null) {
								PreconditionsTagAccessor.v().get(parentMethod).ifPresent((parentPreconditionsTag) -> {
									PreconditionsTagAccessor.v().put(currentMethod, parentPreconditionsTag);
								});
							}
						});

						PostconditionsTagAccessor.v().get(currentMethod).ifPresentOrElse((currentPostconditionsTag) -> {
							if (parentMethod != null) {
								PostconditionsTagAccessor.v().get(parentMethod).ifPresent((parentPostconditionsTag) -> {
									PostconditionsTagAccessor.v().put(
											currentMethod,
											strenghtenPostconditions(parentPostconditionsTag, currentPostconditionsTag));
								});
							}

							introducesSpecification.set(true);
						}, () -> {
							if (parentMethod != null) {
								PostconditionsTagAccessor.v().get(parentMethod).ifPresent((parentPostconditionsTag) -> {
									PostconditionsTagAccessor.v().put(currentMethod, parentPostconditionsTag);
								});
							}
						});

						if (introducesSpecification.get()) {
							parentMethods.put(currentMethodSignature, currentMethod);
						}

						// Handling precondition weakening and postcondition strengthening
						OnlyPreconditionsTagAccessor.v().get(currentMethod).ifPresent((currentPreconditionsTag) -> {
							if (parentMethod != null) {
								PreconditionsTagAccessor.v().get(parentMethod).ifPresent((parentPreconditionsTag) -> {
									final SootMethod vcMethod = new SootMethod(
											currentMethod.getName() + "#precondition_weakening",
											Collections.emptyList(),
											VoidType.v());
									vcMethod.setActiveBody(new JimpleBody());
									final PreconditionsTag preconditionsTag = parentPreconditionsTag;
									final PostconditionsTag postconditionsTag = new PostconditionsTag(
											currentPreconditionsTag.getConditions());
									PreconditionsTagAccessor.v().put(vcMethod, preconditionsTag);
									PostconditionsTagAccessor.v().put(vcMethod, postconditionsTag);
									currentClass.addMethod(vcMethod);
								});
							}
						});

						OnlyPostconditionsTagAccessor.v().get(currentMethod).ifPresent((currentPostconditionTag) -> {
							if (parentMethod != null) {
								PostconditionsTagAccessor.v().get(parentMethod).ifPresent((parentPostconditionsTag) -> {
									final SootMethod vcMethod = new SootMethod(
											currentMethod.getName() + "#postcondition_strenghtening",
											Collections.emptyList(),
											VoidType.v());
									vcMethod.setActiveBody(new JimpleBody());
									final PostconditionsTag postconditionsTag = parentPostconditionsTag;
									final PostconditionsTag preconditionsTag = new PostconditionsTag(
											currentPostconditionTag.getConditions());
									PostconditionsTagAccessor.v().put(vcMethod, preconditionsTag);
									PostconditionsTagAccessor.v().put(vcMethod, postconditionsTag);
									currentClass.addMethod(vcMethod);
								});
							}
						});
					}
				}

				final List<SootClass> childClasses;

				if (currentClass.isInterface()) {
					childClasses = hierarchy.getDirectImplementersOf(currentClass);
				} else {
					childClasses = hierarchy.getDirectSubclassesOf(currentClass);
				}

				for (final SootClass childClass : childClasses) {
					classToParentMethods.put(childClass, new HashMap<>(parentMethods));
					nextClasses.add(childClass);
				}
			}
		}
	}

}
