package byteback.syntax.scene.type.declaration.transformer;

import byteback.common.function.Lazy;
import byteback.syntax.Vimp;
import byteback.syntax.scene.context.SceneContext;
import byteback.syntax.scene.type.TypeType;
import byteback.syntax.scene.type.declaration.context.ClassContext;
import byteback.syntax.scene.type.declaration.member.method.body.value.ForallExpr;
import byteback.syntax.scene.type.declaration.member.method.body.value.TypeConstant;
import byteback.syntax.scene.type.declaration.tag.AxiomsProvider;
import soot.*;
import soot.grimp.Grimp;
import soot.jimple.Jimple;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class HierarchyAxiomTagger extends ClassTransformer {

    private static final Lazy<HierarchyAxiomTagger> instance = Lazy.from(HierarchyAxiomTagger::new);

    public static HierarchyAxiomTagger v() {
        return instance.get();
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
        final FastHierarchy hierarchy = scene.getOrMakeFastHierarchy();
        final List<Value> axiomBoxes = AxiomsProvider.v().getOrCompute(sootClass).getValues();

        for (final SootClass superType : superTypes) {
            final TypeConstant classTypeValue = Vimp.v().newTypeConstant(sootClass.getType());
            final TypeConstant superTypeValue = Vimp.v().newTypeConstant(superType.getType());
            axiomBoxes.add(Vimp.v().newExtendsExpr(classTypeValue, superTypeValue));
        }

        if (!sootClass.isInterface()) {
            final Collection<SootClass> subTypes = hierarchy.getSubclassesOf(sootClass);
            final SootClass[] subTypesArray = subTypes.toArray(new SootClass[0]);

            for (int i = 0; i < subTypesArray.length; ++i) {
                for (int j = i + 1; j < subTypesArray.length; ++j) {
                    final TypeConstant st1 = Vimp.v().newTypeConstant(subTypesArray[i].getType());
                    final TypeConstant st2 = Vimp.v().newTypeConstant(subTypesArray[j].getType());
                    final Local t1Local = Grimp.v().newLocal("t1", TypeType.v());
                    final Local t2Local = Grimp.v().newLocal("t2", TypeType.v());
                    final ForallExpr axiom = Vimp.v().newForallExpr(
                            new Local[]{t1Local, t2Local},
                            Vimp.v().newImpliesExpr(
                                    Jimple.v().newAndExpr(
                                            Vimp.v().nest(
                                                    Vimp.v().newExtendsExpr(t1Local, st1)
                                            ),
                                            Vimp.v().nest(
                                                    Vimp.v().newExtendsExpr(t2Local, st2)
                                            )
                                    ),
                                    Jimple.v().newAndExpr(
                                            Vimp.v().nest(
                                                    Jimple.v().newNegExpr(
                                                            Vimp.v().nest(
                                                                    Vimp.v().newExtendsExpr(t1Local, st2)
                                                            )
                                                    )
                                            ),
                                            Vimp.v().nest(
                                                    Jimple.v().newNegExpr(
                                                            Vimp.v().nest(
                                                                    Vimp.v().newExtendsExpr(t2Local, st1)
                                                            )
                                                    )
                                            )
                                    )
                            )
                    );
                    axiomBoxes.add(axiom);
                }
            }
        }

    }

}
