package byteback.syntax.scene.transformer;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import byteback.common.function.Lazy;
import byteback.syntax.scene.context.SceneContext;
import byteback.syntax.scene.type.declaration.member.method.body.Vimp;
import byteback.syntax.scene.type.declaration.member.method.tag.PostconditionsTag;
import byteback.syntax.scene.type.declaration.member.method.tag.PostconditionsTagAccessor;
import byteback.syntax.scene.type.declaration.member.method.tag.PreconditionsTag;
import byteback.syntax.scene.type.declaration.member.method.tag.PreconditionsTagAccessor;
import soot.Hierarchy;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Value;
import soot.jimple.Jimple;
import soot.util.NumberedString;

/**
 * Propagates method implementations and specification through the @Attach
 * annotation.
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
	public void transformScene(final SceneContext sceneContext) {
		final Scene scene = sceneContext.getScene();
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
					for (final SootMethod currentMethod : currentClass.getMethods()) {
						final NumberedString currentMethodSignature = currentMethod.getNumberedSubSignature();
						final SootMethod parentMethod = parentMethods.get(currentMethodSignature);
						final AtomicBoolean introducesSpecification = new AtomicBoolean(false);

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

					}
				}

				final var currentMethods = parentMethods;
				final List<SootClass> childClasses;

				if (currentClass.isInterface()) {
					childClasses = hierarchy.getDirectImplementersOf(currentClass);
				} else {
					childClasses = hierarchy.getDirectSubclassesOf(currentClass);
				}

				for (final SootClass childClass : childClasses) {
					classToParentMethods.put(childClass, new HashMap<>(currentMethods));
					nextClasses.add(childClass);
				}
			}
		}
	}

}
