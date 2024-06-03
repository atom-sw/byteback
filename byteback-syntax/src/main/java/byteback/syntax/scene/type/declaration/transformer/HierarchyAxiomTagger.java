package byteback.syntax.scene.type.declaration.transformer;

import byteback.common.function.Lazy;
import byteback.syntax.scene.context.SceneContext;
import byteback.syntax.scene.type.TypeType;
import byteback.syntax.scene.type.declaration.context.ClassContext;
import byteback.syntax.scene.type.declaration.member.method.body.Vimp;
import byteback.syntax.scene.type.declaration.member.method.body.value.ForallExpr;
import byteback.syntax.scene.type.declaration.member.method.body.value.TypeConstant;
import byteback.syntax.scene.type.declaration.tag.AxiomsTag;
import byteback.syntax.scene.type.declaration.tag.AxiomsTagAccessor;
import soot.*;
import soot.grimp.Grimp;
import soot.jimple.Jimple;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class HierarchyAxiomTagger extends ClassTransformer {

	private static final Lazy<HierarchyAxiomTagger> INSTANCE = Lazy.from(HierarchyAxiomTagger::new);

	public static HierarchyAxiomTagger v() {
		return INSTANCE.get();
	}

	@Override
	public void transformClass(final ClassContext classContext) {
		final SceneContext sceneContext = classContext.getSceneContext();
		final SootClass sootClass = classContext.getSootClass();
		final Scene scene = sceneContext.getScene();

		if (sootClass.resolvingLevel() < SootClass.HIERARCHY) {
			return;
		}

		final var superTypes = new ArrayList<SootClass>();
		final SootClass superClass = sootClass.getSuperclassUnsafe();

		if (superClass != null) {
			superTypes.add(superClass);
		}

		final Collection<SootClass> superInterfaces = sootClass.getInterfaces();
		superTypes.addAll(superInterfaces);
		final Hierarchy hierarchy = scene.getActiveHierarchy();
		final AxiomsTag axiomsTag = AxiomsTagAccessor.v().putIfAbsent(sootClass, AxiomsTag::new);
		final List<Value> axioms = axiomsTag.getAxioms();

		for (final SootClass superType : superTypes) {
			final TypeConstant classTypeValue = Vimp.v().newTypeConstant(sootClass.getType());
			final TypeConstant superTypeValue = Vimp.v().newTypeConstant(superType.getType());
			axioms.add(Vimp.v().newExtendsExpr(classTypeValue, superTypeValue));
		}

		if (!sootClass.isInterface() && !sootClass.getName().equals("java.lang.Object")) {
			final Collection<SootClass> subTypes = hierarchy.getDirectSubclassesOf(sootClass);
			final SootClass[] subTypesArray = subTypes.toArray(new SootClass[0]);

			for (int i = 0; i < subTypesArray.length; ++i) {
				for (int j = i + 1; j < subTypesArray.length; ++j) {
					final TypeConstant subType1 = Vimp.v().newTypeConstant(subTypesArray[i].getType());
					final TypeConstant subType2 = Vimp.v().newTypeConstant(subTypesArray[j].getType());
					final Local t1Local = Grimp.v().newLocal("t1", TypeType.v());
					final Local t2Local = Grimp.v().newLocal("t2", TypeType.v());
					final ForallExpr axiom1 = Vimp.v().newForallExpr(
							new Local[] { t1Local },
							new Value[] {
									Vimp.v().nest(
											Vimp.v().newExtendsExpr(t1Local, subType1))
							},
							Vimp.v().newImpliesExpr(
									Vimp.v().nest(
											Vimp.v().newExtendsExpr(t1Local, subType1)),
									Vimp.v().nest(
											Jimple.v().newNegExpr(
													Vimp.v().nest(
															Vimp.v().newExtendsExpr(t1Local, subType2))))));
					axioms.add(axiom1);

					final ForallExpr axiom2 = Vimp.v().newForallExpr(
							new Local[] { t2Local },
							new Value[] {
									Vimp.v().nest(
											Vimp.v().newExtendsExpr(t2Local, subType2))
							},
							Vimp.v().newImpliesExpr(
									Vimp.v().nest(
											Vimp.v().nest(
													Vimp.v().newExtendsExpr(t2Local, subType2))),
									Vimp.v().nest(
											Vimp.v().nest(
													Jimple.v().newNegExpr(
															Vimp.v().nest(
																	Vimp.v().newExtendsExpr(t2Local, subType1)))))));
					axioms.add(axiom2);
				}
			}
		}

	}

}
