package byteback.syntax.name;

import byteback.common.function.Lazy;
import soot.SootClass;

/**
 * Contains the definitions of the names for the BBLib specification.
 *
 * @author paganma
 */
public class BBLibNames {

    public static final Lazy<BBLibNames> INSTANCE = Lazy.from(BBLibNames::new);

    public static BBLibNames v() {
        return INSTANCE.get();
    }

    private BBLibNames() {
    }

    public static final String CONTRACT_CLASS_NAME = "byteback.specification.Contract";

    public static final String QUANTIFIER_CLASS_NAME = "byteback.specification.Quantifier";

    public static final String SPECIAL_CLASS_NAME = "byteback.specification.Special";

    public static final String BINDING_CLASS_NAME = "byteback.specification.Binding";

    public static final String LEMMA_ANNOTATION = "Lbyteback/specification/Contract$Lemma;";

    public static final String BEHAVIOR_ANNOTATION = "Lbyteback/specification/Contract$Behavior;";

    public static final String PRELUDE_ANNOTATION = "Lbyteback/specification/Contract$Prelude;";

    public static final String REQUIRE_ANNOTATION = "Lbyteback/specification/Contract$Require;";

    public static final String REQUIRES_ANNOTATION = "Lbyteback/specification/Contract$Requires;";

    public static final String ENSURE_ANNOTATION = "Lbyteback/specification/Contract$Ensure;";

    public static final String ENSURES_ANNOTATION = "Lbyteback/specification/Contract$Ensures;";

    public static final String RAISE_ANNOTATION = "Lbyteback/specification/Contract$Raise;";

    public static final String RAISES_ANNOTATION = "Lbyteback/specification/Contract$Raises;";

    public static final String RETURN_ANNOTATION = "Lbyteback/specification/Contract$Return;";

    public static final String OPERATOR_ANNOTATION = "Lbyteback/specification/Contract$Operator;";

    public static final String ATTACH_ANNOTATION = "Lbyteback/specification/Contract$Attach;";

    public static final String IGNORE_ANNOTATION = "Lbyteback/specification/Contract$Ignore;";

    public static final String INVARIANT_ANNOTATION = "Lbyteback/specification/Contract$Invariant;";

    public static final String OLD_NAME = "old";

    public static final String INVARIANT_NAME = "invariant";

    public static final String ASSUMPTION_NAME = "assumption";

    public static final String ASSERTION_NAME = "assertion";

    public static final String CONDITIONAL_NAME = "conditional";

    public static final String UNIVERSAL_QUANTIFIER_NAME = "forall";

    public static final String EXISTENTIAL_QUANTIFIER_NAME = "exists";

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

}