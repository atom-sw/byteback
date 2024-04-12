package byteback.syntax.name;

import byteback.syntax.tag.AnnotationReader;
import byteback.common.function.Lazy;
import soot.SootClass;
import soot.SootMethod;

/**
 * Contains the definitions of the names for the BBLib specification.
 *
 * @author paganma
 */
public class BBLibNames {

    public static final String ANNOTATION_PACKAGE = "byteback.specification";

    public static final String CONTRACT_CLASS_NAME = "byteback.specification.Contract";

    public static final String QUANTIFIER_CLASS_NAME = "byteback.specification.Quantifier";

    public static final String SPECIAL_CLASS_NAME = "byteback.specification.Special";

    public static final String BINDING_CLASS_NAME = "byteback.specification.Binding";

    public static final String LEMMA_ANNOTATION = "Lbyteback/specification/Contract$Lemma;";

    public static final String PRELUDE_ANNOTATION = "Lbyteback/specification/Contract$Prelude;";

    public static final String BEHAVIOR_ANNOTATION = "Lbyteback/specification/Contract$Behavior;";

    public static final String REQUIRE_ANNOTATION = "Lbyteback/specification/Contract$Require;";

    public static final String REQUIRES_ANNOTATION = "Lbyteback/specification/Contract$Requires;";

    public static final String ENSURE_ANNOTATION = "Lbyteback/specification/Contract$Ensure;";

    public static final String ENSURES_ANNOTATION = "Lbyteback/specification/Contract$Ensures;";

    public static final String RAISE_ANNOTATION = "Lbyteback/specification/Contract$Raise;";

    public static final String RAISES_ANNOTATION = "Lbyteback/specification/Contract$Raises;";

    public static final String RETURN_ANNOTATION = "Lbyteback/specification/Contract$Return;";

    public static final String PRIMITIVE_ANNOTATION = "Lbyteback/specification/Contract$Primitive;";

    public static final String ATTACH_ANNOTATION = "Lbyteback/specification/Contract$Attach;";

    public static final String IGNORE_ANNOTATION = "Lbyteback/specification/Contract$Ignore;";

    public static final String INVARIANT_ANNOTATION = "Lbyteback/specification/Contract$Invariant;";

    public static final String ATTACH_LABEL_ANNOTATION = "Lbyteback/specification/Contract$AttachLabel;";

    public static final String MODEL_NPE_ANNOTATION = "Lbyteback/specification/Contract$ModelNPE;";

    public static final String MODEL_IOBE_ANNOTATION = "Lbyteback/specification/Contract$ModelIOBE;";

    public static final String OLD_NAME = "old";

    public static final String INVARIANT_NAME = "invariant";

    public static final String ASSUMPTION_NAME = "assumption";

    public static final String ASSERTION_NAME = "assertion";

    public static final String CONDITIONAL_NAME = "conditional";

    public static final String UNIVERSAL_QUANTIFIER_NAME = "forall";

    public static final String EXISTENTIAL_QUANTIFIER_NAME = "exists";

    public static final Lazy<BBLibNames> instance = Lazy.from(BBLibNames::new);

    public static BBLibNames v() {
        return instance.get();
    }

    private BBLibNames() {
    }

    public boolean isContractClass(final SootClass sootClass) {
        return sootClass.getName().equals(CONTRACT_CLASS_NAME);
    }

    public boolean isQuantifierClass(final SootClass sootClass) {
        return sootClass.getName().equals(QUANTIFIER_CLASS_NAME);
    }

    public boolean isSpecialClass(final SootClass sootClass) {
        return sootClass.getName().equals(SPECIAL_CLASS_NAME);
    }

    public boolean isBindingClass(final SootClass sootClass) {
        return sootClass.getName().equals(BINDING_CLASS_NAME);
    }

    public boolean isAnnotationClass(final SootClass sootClass) {
        return sootClass.getPackageName().equals(ANNOTATION_PACKAGE);
    }

    public boolean isFunctionMethod(final SootMethod method) {
        return AnnotationReader.v().hasAnnotation(method, BEHAVIOR_ANNOTATION);
    }

    public boolean isPredicateMethod(final SootMethod method) {
        return AnnotationReader.v().hasAnnotation(method, BEHAVIOR_ANNOTATION);
    }

}
