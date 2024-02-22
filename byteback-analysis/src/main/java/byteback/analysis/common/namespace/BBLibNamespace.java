package byteback.analysis.common.namespace;

import byteback.analysis.model.SootHosts;
import soot.SootClass;
import soot.SootMethod;

/**
 * Contains the definitions of the names for the BBLib annotations.
 */
public class BBLibNamespace {

	public static final String ANNOTATION_PACKAGE = "byteback.annotations";

	public static final String CONTRACT_CLASS_NAME = "byteback.specification.Contract";

	public static final String QUANTIFIER_CLASS_NAME = "byteback.specification.Quantifier";

	public static final String SPECIAL_CLASS_NAME = "byteback.specification.Special";

	public static final String BINDING_CLASS_NAME = "byteback.specification.Binding";

	public static final String LEMMA_ANNOTATION = "Lbyteback/specification/Contract$Lemma;";

	public static final String PRELUDE_ANNOTATION = "Lbyteback/specification/Contract$Prelude;";

	public static final String PURE_ANNOTATION = "Lbyteback/specification/Contract$Pure;";

	public static final String PREDICATE_ANNOTATION = "Lbyteback/specification/Contract$Predicate;";

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

	public static String stripDescriptor(final String descriptor) {
		return descriptor.substring(1, descriptor.length() - 1).replace("/", ".");
	}

	public static String stripLabelDescriptor(final String descriptor) {
		return stripDescriptor(descriptor.substring(1));
	}

	public static String stripConstantDescriptor(final String descriptor) {
		return stripDescriptor(descriptor.replace("[", "").replace("]", ""));
	}

	public static boolean isContractClass(final SootClass clazz) {
		return clazz.getName().equals(CONTRACT_CLASS_NAME);
	}

	public static boolean isQuantifierClass(final SootClass clazz) {
		return clazz.getName().equals(QUANTIFIER_CLASS_NAME);
	}

	public static boolean isSpecialClass(final SootClass clazz) {
		return clazz.getName().equals(SPECIAL_CLASS_NAME);
	}

	public static boolean isBindingClass(final SootClass clazz) {
		return clazz.getName().equals(BINDING_CLASS_NAME);
	}

	public static boolean isAnnotationClass(final SootClass clazz) {
		return clazz.getPackageName().equals(ANNOTATION_PACKAGE);
	}

	public static boolean isPureMethod(final SootMethod method) {
		return SootHosts.hasAnnotation(method, PURE_ANNOTATION);
	}

	public static boolean isPredicateMethod(final SootMethod method) {
		return SootHosts.hasAnnotation(method, PREDICATE_ANNOTATION);
	}

}
