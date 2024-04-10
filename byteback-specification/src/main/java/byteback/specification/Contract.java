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
	public @interface Behavior {
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
		Class<?> exception();
		String when() default "[unassigned]";
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.METHOD, ElementType.CONSTRUCTOR})
	public @interface Raises {
		Raise[] value();
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.METHOD, ElementType.CONSTRUCTOR})
	public @interface Return {
		String when() default "[unassigned]";
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.METHOD, ElementType.CONSTRUCTOR})
	public @interface Returns {
		Return[] value();
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.METHOD})
	public @interface Isolated {
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.TYPE})
	public @interface Attach {
		String value();
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.METHOD})
	public @interface Ignore {
	}

	public static void assertion(boolean behavior) {
	}

	public static void assumption(boolean behavior) {
	}

	public static void invariant(boolean behavior) {
	}

}
