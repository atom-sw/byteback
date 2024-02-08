package byteback.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotations for the specification of contracts.
 */
public interface Contract {

	/**
	 * Declares a predicate method.
	 *
	 * A predicate method is a completely pure static method containing boolean
	 * statements verifying conditions on its inputs. A predicate method may only
	 * indirectly call other static predicate methods.
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.METHOD})
	public static @interface Pure {
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.METHOD})
	public @interface Primitive {
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.METHOD, ElementType.CONSTRUCTOR})
	public @interface Lemma {
	}

	/**
	 * Declares that the function is already defined in the preamble.
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.METHOD})
	public static @interface Prelude {
		public String value();
	}

	@Repeatable(Requires.class)
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.METHOD, ElementType.CONSTRUCTOR})
	public static @interface Require {
		public String value();
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.METHOD, ElementType.CONSTRUCTOR})
	public @interface Requires {
		public Require[] value();
	}

	@Repeatable(Ensures.class)
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.METHOD, ElementType.CONSTRUCTOR})
	public @interface Ensure {
		public String value();
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.METHOD, ElementType.CONSTRUCTOR})
	public @interface Ensures {
		public Ensure[] value();
	}

	@Repeatable(Raises.class)
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.METHOD, ElementType.CONSTRUCTOR})
	public @interface Raise {
		public Class<?> exception();
		public String when() default "[unassigned]";
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.METHOD, ElementType.CONSTRUCTOR})
	public @interface Raises {
		public Raise[] value();
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.METHOD, ElementType.CONSTRUCTOR})
	public @interface Return {
		public String when() default "[unassigned]";
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.METHOD, ElementType.CONSTRUCTOR})
	public @interface Returns {
		public Return[] value();
	}

	/**
	 * Declares a predicate method.
	 * <p>
	 * Condition methods can be used to represent preconditions, postconditions or
	 * invariants. Every condition method must be both, static and pure. The return
	 * type of the annotated method must be void, as conditions are enforced by
	 * means of assertions. A condition method may only call predicate methods.
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.METHOD})
	public static @interface Predicate {
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.METHOD})
	public static @interface Isolated {
	}

	public static @interface Attach {
		public Class<?> value();
	}

	public static @interface AttachLabel {
		public String value();
	}

	public static void assertion(boolean condition) {
	}

	public static void assumption(boolean condition) {
	}

	public static void invariant(boolean condition) {
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.METHOD})
	public static @interface Ignore {
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.METHOD, ElementType.CONSTRUCTOR})
	public static @interface Invariant {
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.METHOD})
	public static @interface ModelNPE {
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.METHOD})
	public static @interface ModelIOBE {
	}

}
