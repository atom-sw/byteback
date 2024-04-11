package byteback.analysis.global.vimp.transformer;

import byteback.analysis.global.common.transformer.ClassTransformer;
import byteback.analysis.global.vimp.syntax.type.TypeType;
import byteback.analysis.global.vimp.syntax.value.TypeConstant;
import byteback.analysis.global.vimp.tag.AxiomsProvider;
import byteback.analysis.local.vimp.syntax.Vimp;
import byteback.analysis.local.vimp.syntax.value.ForallExpr;
import byteback.analysis.local.vimp.syntax.value.box.ConditionExprBox;
import byteback.common.function.Lazy;
import soot.*;
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
    public void transformClass(final Scene scene, final SootClass sootClass) {
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
        final Collection<SootClass> subTypes = hierarchy.getSubclassesOf(sootClass);
        final SootClass[] subTypesArray = subTypes.toArray(new SootClass[0]);
        final List<ConditionExprBox> axiomBoxes = AxiomsProvider.v().getOrCompute(sootClass).getValueBoxes();

        for (final SootClass superType : superTypes) {
            final TypeConstant classTypeValue = Vimp.v().newTypeConstant(sootClass.getType());
            final TypeConstant superTypeValue = Vimp.v().newTypeConstant(superType.getType());
            axiomBoxes.add(new ConditionExprBox(Vimp.v().newExtendsExpr(classTypeValue, superTypeValue)));
        }

        for (int i = 0; i < subTypesArray.length; ++i) {
            for (int j = i + 1; j < subTypesArray.length; ++j) {
                final TypeConstant st1 = Vimp.v().newTypeConstant(subTypesArray[i].getType());
                final TypeConstant st2 = Vimp.v().newTypeConstant(subTypesArray[j].getType());
                final Local rt1 = Jimple.v().newLocal("rt1", TypeType.v());
                final Local rt2 = Jimple.v().newLocal("rt2", TypeType.v());
                final ForallExpr axiom = Vimp.v().newForallExpr(
                        new Local[]{ rt1, rt2 },
                        Vimp.v().newImpliesExpr(
                                Jimple.v().newAndExpr(
                                        Vimp.v().newExtendsExpr(rt1, st1),
                                        Vimp.v().newExtendsExpr(rt2, st2)
                                ),
                                Jimple.v().newAndExpr(
                                        Jimple.v().newNegExpr(
                                                Vimp.v().newExtendsExpr(rt1, st2)
                                        ),
                                        Jimple.v().newNegExpr(
                                                Vimp.v().newExtendsExpr(rt2, st1)
                                        )
                                )
                        )
                );
            }
        }

    }

}
