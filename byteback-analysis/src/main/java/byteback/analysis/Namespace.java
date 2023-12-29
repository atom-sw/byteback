package byteback.analysis;

import byteback.analysis.util.SootHosts;
import soot.SootClass;
import soot.SootMethod;

/**
 * Utility class containing the definitions of the special annotations necessary
 * for the conversion between Jimple and Boogie.
 */
public class Namespace {

	/**
	 * All the annotation classes must be located under this package. The classes
	 * are provided by the byteback-annotations subproject.
	 */
	public static final String ANNOTATION_PACKAGE = "byteback.annotations";

	public static final String CONTRACT_CLASS_NAME = "byteback.annotations.Contract";

	public static final String QUANTIFIER_CLASS_NAME = "byteback.annotations.Quantifier";

	public static final String SPECIAL_CLASS_NAME = "byteback.annotations.Special";

	public static final String BINDING_CLASS_NAME = "byteback.annotations.Binding";

	public static final String LEMMA_ANNOTATION = "Lbyteback/annotations/Contract$Lemma;";

	public static final String PRELUDE_ANNOTATION = "Lbyteback/annotations/Contract$Prelude;";

	public static final String PURE_ANNOTATION = "Lbyteback/annotations/Contract$Pure;";

	public static final String PREDICATE_ANNOTATION = "Lbyteback/annotations/Contract$Predicate;";

	public static final String REQUIRE_ANNOTATION = "Lbyteback/annotations/Contract$Require;";

	public static final String REQUIRES_ANNOTATION = "Lbyteback/annotations/Contract$Requires;";

	public static final String ENSURE_ANNOTATION = "Lbyteback/annotations/Contract$Ensure;";

	public static final String ENSURES_ANNOTATION = "Lbyteback/annotations/Contract$Ensures;";

	public static final String RAISE_ANNOTATION = "Lbyteback/annotations/Contract$Raise;";

	public static final String RAISES_ANNOTATION = "Lbyteback/annotations/Contract$Raises;";

	public static final String RETURN_ANNOTATION = "Lbyteback/annotations/Contract$Return;";

	public static final String PRIMITIVE_ANNOTATION = "Lbyteback/annotations/Contract$Primitive;";

	public static final String ATTACH_ANNOTATION = "Lbyteback/annotations/Contract$Attach;";

	public static final String IGNORE_ANNOTATION = "Lbyteback/annotations/Contract$Ignore;";

	public static final String INVARIANT_ANNOTATION = "Lbyteback/annotations/Contract$Invariant;";

	public static final String ATTACH_LABEL_ANNOTATION = "Lbyteback/annotations/Contract$AttachLabel;";

	public static final String MODEL_NPE_ANNOTATION = "Lbyteback/annotations/Contract$ModelNPE;";

	public static final String MODEL_IOBE_ANNOTATION = "Lbyteback/annotations/Contract$ModelIOBE;";

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
		return stripDescriptor(descriptor.substring(1, descriptor.length()));
	}

	public static String stripConstantDescriptor(final String descriptor) {
		return stripDescriptor(descriptor.replace("[", "").replace("]", ""));
	}

	/**
	 * Checks if a class is the {@link byteback.annotations.Contract} class.
	 *
	 * @param clazz
	 *            The class to be checked.
	 * @return {@true} if the class is {@link byteback.annotations.Contract}.
	 */
	public static boolean isContractClass(final SootClass clazz) {
		return clazz.getName().equals(CONTRACT_CLASS_NAME);
	}

	/**
	 * Checks if a class is the {@link byteback.annotations.Quantifier} class.
	 *
	 * @param clazz
	 *            The class to be checked.
	 * @return {@true} if the class is {@link byteback.annotations.Quantifier}.
	 */
	public static boolean isQuantifierClass(final SootClass clazz) {
		return clazz.getName().equals(QUANTIFIER_CLASS_NAME);
	}

	/**
	 * Checks if a class is the {@link byteback.annotations.Special} class.
	 *
	 * @param clazz
	 *            The class to be checked.
	 * @return {@true} if the class is {@link byteback.annotations.Special}.
	 */
	public static boolean isSpecialClass(final SootClass clazz) {
		return clazz.getName().equals(SPECIAL_CLASS_NAME);
	}

	/**
	 * Checks if a class is the {@link byteback.annotations.Binding} class.
	 *
	 * @param clazz
	 *            The class to be checked.
	 * @return {@true} if the class is {@link byteback.annotations.Binding}.
	 */
	public static boolean isBindingClass(final SootClass clazz) {
		return clazz.getName().equals(BINDING_CLASS_NAME);
	}

	/**
	 * Checks if a class is part of the {@link byteback.annotations} package.
	 *
	 * @param clazz
	 *            The class to be checked.
	 * @return {@true} if the class is part of {@link byteback.annotations}.
	 */
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
