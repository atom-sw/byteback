package byteback.syntax.scene.transformer;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import byteback.common.function.Lazy;
import byteback.syntax.scene.type.declaration.member.method.body.Vimp;
import byteback.syntax.scene.type.declaration.member.method.body.value.ReturnRef;
import byteback.syntax.scene.type.declaration.member.method.tag.EnsureOnlyTagMarker;
import byteback.syntax.scene.type.declaration.member.method.tag.RequireOnlyTagMarker;
import byteback.syntax.scene.type.declaration.member.method.tag.PostconditionsTag;
import byteback.syntax.scene.type.declaration.member.method.tag.PostconditionsTagAccessor;
import byteback.syntax.scene.type.declaration.member.method.tag.PreconditionsTag;
import byteback.syntax.scene.type.declaration.member.method.tag.PreconditionsTagAccessor;
import byteback.syntax.scene.type.declaration.tag.InvariantMethodsTag;
import byteback.syntax.scene.type.declaration.tag.InvariantMethodsTagAccessor;
import byteback.syntax.scene.type.declaration.tag.InvariantOnlyTagMarker;
import byteback.syntax.scene.type.declaration.tag.InvariantsTag;
import byteback.syntax.scene.type.declaration.tag.InvariantsTagAccessor;
import soot.Hierarchy;
import soot.Scene;
import soot.Type;
import soot.SootClass;
import soot.SootMethod;
import soot.Value;
import soot.ValueBox;
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

	private List<SootClass> superTypesOf(final SootClass sootClass) {
		final var superTypes = new ArrayList<SootClass>();

		if (!sootClass.isInterface()) {
			final SootClass superClass = sootClass.getSuperclassUnsafe();

			if (superClass != null) {
				superTypes.add(superClass);
			}
		}

		superTypes.addAll(sootClass.getInterfaces());

		return superTypes;
	}

	private List<SootClass> subTypesOf(final Hierarchy hierarchy, final SootClass sootClass) {
		final var subTypes = new ArrayList<SootClass>();

		if (sootClass.isInterface()) {
			subTypes.addAll(hierarchy.getDirectImplementersOf(sootClass));
			subTypes.addAll(hierarchy.getDirectSubinterfacesOf(sootClass));
		} else {
			subTypes.addAll(hierarchy.getDirectSubclassesOf(sootClass));
		}

		return subTypes;
	}

	private ArrayList<SootClass> topologicalSort(final Scene scene, final Hierarchy hierarchy) {
		final var sortedClasses = new ArrayList<SootClass>();
		final var visitedClasses = new HashSet<>();
		final var nextClasses = new ArrayDeque<SootClass>();

		for (final SootClass startingClass : scene.getClasses()) {
			if ((startingClass.resolvingLevel() < SootClass.HIERARCHY)
					||
					(startingClass.isConcrete() &&
							(startingClass.getSuperclassUnsafe() != null))
					||
					(startingClass.isInterface() &&
							(!startingClass.getSuperclass().getName().equals("java.lang.Object")))
					||
					startingClass.getInterfaceCount() != 0) {
				continue;
			}

			nextClasses.add(startingClass);
		}

		while (!nextClasses.isEmpty()) {
			final SootClass currentClass = nextClasses.pop();
			sortedClasses.add(currentClass);
			visitedClasses.add(currentClass);

			for (final SootClass subClass : subTypesOf(hierarchy, currentClass)) {

				if (!visitedClasses.contains(subClass) && visitedClasses.containsAll(superTypesOf(subClass))) {
					nextClasses.add(subClass);
				}
			}
		}

		return sortedClasses;
	}

	private HashSet<SootMethod> resolveParentMethods(final SootMethod sootMethod) {
		final SootClass sootClass = sootMethod.getDeclaringClass();
		final NumberedString signature = sootMethod.getNumberedSubSignature();
		final var nextClasses = new ArrayDeque<SootClass>(superTypesOf(sootClass));
		final var parentMethods = new HashSet<SootMethod>();

		nextClasses.addAll(superTypesOf(sootClass));

		while (!nextClasses.isEmpty()) {
			final SootClass currentClass = nextClasses.pop();
			final SootMethod parentMethod = currentClass.getMethodUnsafe(signature);

			if (parentMethod != null) {
				parentMethods.add(parentMethod);
			} else {
				nextClasses.addAll(superTypesOf(currentClass));
			}
		}

		return parentMethods;
	}

	@Override
	public void transformScene(final Scene scene) {
		final Hierarchy hierarchy = scene.getActiveHierarchy();
		final List<SootClass> sortedClasses = topologicalSort(scene, hierarchy);

		for (final SootClass sootClass : sortedClasses) {

			if (sootClass.resolvingLevel() < SootClass.SIGNATURES) {
				continue;
			}

			for (final SootClass parentClass : superTypesOf(sootClass)) {
				if (!InvariantOnlyTagMarker.v().hasTag(sootClass)) {
					InvariantsTagAccessor.v().get(parentClass).ifPresent((invariantsTag) -> {
						InvariantsTagAccessor.v().putIfAbsent(sootClass, InvariantsTag::new)
								.addConditionBoxes(invariantsTag.getConditionBoxes());
					});
					InvariantMethodsTagAccessor.v().get(parentClass).ifPresent((invariantMethodsTag) -> {
						InvariantMethodsTagAccessor.v().putIfAbsent(sootClass, InvariantMethodsTag::new)
								.addInvariantMethods(invariantMethodsTag.getInvariantMethods());
					});
				} else {
					InvariantsTagAccessor.v().get(parentClass).ifPresent((parentInvariantTag) -> {
						InvariantsTagAccessor.v().get(sootClass).ifPresent((currentInvariantTag) -> {
							final SootMethod vcMethod = new SootMethod(
									"inv?strenghtening",
									Collections.emptyList(),
									VoidType.v());
							vcMethod.setActiveBody(new JimpleBody());
							final PostconditionsTag postconditionsTag = new PostconditionsTag(
									parentInvariantTag.getConditions());
							final PreconditionsTag preconditionsTag = new PreconditionsTag(
									currentInvariantTag.getConditions());
							PreconditionsTagAccessor.v().put(vcMethod, preconditionsTag);
							PostconditionsTagAccessor.v().put(vcMethod, postconditionsTag);
							sootClass.addMethod(vcMethod);
						});
					});
				}
			}

			for (final SootMethod targetMethod : new ArrayList<>(sootClass.getMethods())) {
				if (targetMethod.isConstructor()) {
					continue;
				}

				for (final SootMethod parentMethod : resolveParentMethods(targetMethod)) {

					if (RequireOnlyTagMarker.v().hasTag(targetMethod)) {
						PreconditionsTagAccessor.v().get(parentMethod).ifPresent((parentPreconditionsTag) -> {
							PreconditionsTagAccessor.v().get(targetMethod).ifPresent((currentPreconditionsTag) -> {
								final SootMethod vcMethod = new SootMethod(
										targetMethod.getName() + "?weakening",
										targetMethod.getParameterTypes(),
										VoidType.v());
								vcMethod.setActiveBody(new JimpleBody());
								final PreconditionsTag preconditionsTag = parentPreconditionsTag;
								final PostconditionsTag postconditionsTag = new PostconditionsTag(
										currentPreconditionsTag.getConditions());
								PreconditionsTagAccessor.v().put(vcMethod, preconditionsTag);
								PostconditionsTagAccessor.v().put(vcMethod, postconditionsTag);
								sootClass.addMethod(vcMethod);
							});
						});
					} else {
						PreconditionsTagAccessor.v().get(targetMethod).ifPresentOrElse((currentPreconditionsTag) -> {
							PreconditionsTagAccessor.v().get(parentMethod).ifPresent((parentPreconditionsTag) -> {
								PreconditionsTagAccessor.v().put(
										targetMethod,
										weakenPreconditions(parentPreconditionsTag, currentPreconditionsTag));
							});
						}, () -> {
							PreconditionsTagAccessor.v().get(parentMethod).ifPresent((parentPreconditionsTag) -> {
								PreconditionsTagAccessor.v().put(
										targetMethod,
										parentPreconditionsTag);
							});
						});
					}
					if (EnsureOnlyTagMarker.v().hasTag(targetMethod)) {
						PostconditionsTagAccessor.v().get(parentMethod).ifPresent((parentPostconditionsTag) -> {
							PostconditionsTagAccessor.v().get(targetMethod).ifPresent((currentPostconditionsTag) -> {
								final var parameterTypes = new ArrayList<Type>();
								final PostconditionsTag postconditionsTag = new PostconditionsTag();
								Value lhs = null;
								Value rhs = null;

								for (final Value currentPostcondition : currentPostconditionsTag.getConditions()) {
									if (lhs == null) {
										lhs = currentPostcondition;
									} else {
										lhs = Jimple.v().newAndExpr(lhs, currentPostcondition);
									}
								}

								for (final Value parentPostcondition : parentPostconditionsTag.getConditions()) {
									if (rhs == null) {
										rhs = parentPostcondition;
									} else {
										rhs = Jimple.v().newAndExpr(lhs, parentPostcondition);
									}
								}

								final SootMethod vcMethod = new SootMethod(
										targetMethod.getName() + "?strenghtening",
										parameterTypes,
										targetMethod.getReturnType());
								vcMethod.setActiveBody(new JimpleBody());
								postconditionsTag.addCondition(Vimp.v().newImpliesExpr(Vimp.v().nest(lhs), Vimp.v().nest(rhs)));
								PostconditionsTagAccessor.v().put(vcMethod, postconditionsTag);
								sootClass.addMethod(vcMethod);
							});
						});
					} else {
						PostconditionsTagAccessor.v().get(targetMethod).ifPresentOrElse((currentPostconditionsTag) -> {
							PostconditionsTagAccessor.v().get(parentMethod).ifPresent((parentPostconditionsTag) -> {
								PostconditionsTagAccessor.v().put(
										targetMethod,
										strenghtenPostconditions(parentPostconditionsTag, currentPostconditionsTag));
							});
						}, () -> {
							PostconditionsTagAccessor.v().get(parentMethod).ifPresent((parentPostconditionsTag) -> {
								PostconditionsTagAccessor.v().put(
										targetMethod,
										parentPostconditionsTag);
							});
						});
					}
				}
			}
		}
	}
}
