package byteback.specification;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotations for the specification of contracts.
 */
public class Contract {

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.METHOD})
	public @interface Predicate {
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.METHOD})
	public @interface Function {
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
	public @interface Prelude {
		public String value();
	}

	@Repeatable(Requires.class)
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.METHOD, ElementType.CONSTRUCTOR})
	public @interface Require {
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

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.METHOD})
	public @interface Isolated {
	}

	public @interface Attach {
		Class<?> value();
	}

	public @interface AttachLabel {
		String value();
	}

	public static void assertion(boolean behavior) {
	}

	public static void assumption(boolean behavior) {
	}

	public static void invariant(boolean behavior) {
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.METHOD})
	public @interface Ignore {
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.METHOD, ElementType.CONSTRUCTOR})
	public @interface Invariant {
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.METHOD})
	public @interface ModelNPE {
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.METHOD})
	public @interface ModelIOBE {
	}

}
