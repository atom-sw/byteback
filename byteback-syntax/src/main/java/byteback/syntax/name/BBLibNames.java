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

	private BBLibNames() {
	}

	public static BBLibNames v() {
		return INSTANCE.get();
	}

	/** Ghost code annotations */
	public static final String GHOST_CLASS_NAME = "byteback.specification.ghost.Ghost";

	public static final String ATTACH_ANNOTATION = "Lbyteback/specification/ghost/Ghost$Attach;";

	public static final String EXPORT_ANNOTATION = "Lbyteback/specification/ghost/Ghost$Export;";

	public static final String IMPORT_ANNOTATION = "Lbyteback/specification/ghost/Ghost$Import;";

	public static final String OF_NAME = "of";

	/** Behavior annotations */
	public static final String BEHAVIOR_ANNOTATION = "Lbyteback/specification/Contract$Behavior;";

	public static final String TWOSTATE_ANNOTATION = "Lbyteback/specification/Contract$TwoState;";

	public static final String EXCEPTIONAL_ANNOTATION = "Lbyteback/specification/Contract$Exceptional;";

	public static final String OPERATOR_ANNOTATION = "Lbyteback/specification/Contract$Operator;";

	public static final String PRELUDE_ANNOTATION = "Lbyteback/specification/Contract$Prelude;";

	/** Behavior body constructs */
	public static final String BINDINGS_CLASS_NAME = "byteback.specification.Bindings";

	public static final String CONTRACT_CLASS_NAME = "byteback.specification.Contract";

	public static final String QUANTIFIERS_CLASS_NAME = "byteback.specification.Quantifiers";

	public static final String EXISTENTIAL_QUANTIFIER_NAME = "exists";

	public static final String UNIVERSAL_QUANTIFIER_NAME = "forall";

	public static final String SPECIAL_CLASS_NAME = "byteback.specification.Special";

	public static final String CONDITIONAL_NAME = "conditional";

	public static final String OLD_NAME = "old";

	public static final String THROWN_NAME = "thrown";

	/** Class annotations */
	public static final String CLASS_INVARIANT_ANNOTATION = "Lbyteback/specification/Contract$Invariant;";

	/** Procedure annotations */
	public static final String ENSURES_ANNOTATION = "Lbyteback/specification/Contract$Ensures;";

	public static final String ENSURE_ANNOTATION = "Lbyteback/specification/Contract$Ensure;";

	public static final String ENSURE_ONLY_ANNOTATION = "Lbyteback/specification/Contract$EnsureOnly;";

	public static final String IGNORE_ANNOTATION = "Lbyteback/specification/Contract$Ignore;";

	public static final String ABSTRACT_ANNOTATION = "Lbyteback/specification/Contract$Abstract;";

	public static final String RAISE_ANNOTATION = "Lbyteback/specification/Contract$Raise;";

	public static final String REQUIRE_ANNOTATION = "Lbyteback/specification/Contract$Require;";

	public static final String REQUIRE_ONLY_ANNOTATION = "Lbyteback/specification/Contract$RequireOnly;";

	public static final String RETURN_ANNOTATION = "Lbyteback/specification/Contract$Return;";

	public static final String PURE_ANNOTATION = "Lbyteback/specification/Contract$Pure;";

	/** Procedural specification constructs */
	public static final String INVARIANT_NAME = "invariant";

	public static final String ASSERTION_NAME = "assertion";

	public static final String ASSUMPTION_NAME = "assumption";

	/** Predicates for special classes */
	public boolean isBindingsClass(final SootClass sootClass) {
		return sootClass.getName().equals(BINDINGS_CLASS_NAME);
	}

	public boolean isContractClass(final SootClass sootClass) {
		return sootClass.getName().equals(CONTRACT_CLASS_NAME);
	}

	public boolean isQuantifiersClass(final SootClass sootClass) {
		return sootClass.getName().equals(QUANTIFIERS_CLASS_NAME);
	}

	public boolean isSpecialClass(final SootClass sootClass) {
		return sootClass.getName().equals(SPECIAL_CLASS_NAME);
	}

	public boolean isGhostClass(final SootClass sootClass) {
		return sootClass.getName().equals(GHOST_CLASS_NAME);
	}

}
