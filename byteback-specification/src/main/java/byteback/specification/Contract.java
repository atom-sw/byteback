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
	@Target({ ElementType.METHOD })
	public @interface Implicit {
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.METHOD })
	public @interface Behavior {
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.METHOD })
	public @interface Operator {
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.METHOD })
	public @interface Exceptional {
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.METHOD })
	public @interface TwoState {
	}

	/**
	 * Declares that the function is already defined in the preamble.
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.METHOD })
	public @interface Prelude {
		String value();
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.TYPE })
	public @interface Invariant {
		String value();
	}

	@Repeatable(Requires.class)
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.METHOD, ElementType.CONSTRUCTOR })
	public @interface Require {
		String value();
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.METHOD, ElementType.CONSTRUCTOR })
	public @interface Requires {
		Require[] value();
	}

	@Repeatable(RequireOnlys.class)
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.METHOD, ElementType.CONSTRUCTOR })
	public @interface RequireOnly {
		String value();
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.METHOD, ElementType.CONSTRUCTOR })
	public @interface RequireOnlys {
		RequireOnly[] value();
	}

	@Repeatable(Ensures.class)
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.METHOD, ElementType.CONSTRUCTOR })
	public @interface Ensure {
		String value();
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.METHOD, ElementType.CONSTRUCTOR })
	public @interface Ensures {
		Ensure[] value();
	}

	@Repeatable(EnsureOnlys.class)
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.METHOD, ElementType.CONSTRUCTOR })
	public @interface EnsureOnly {
		String value();
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.METHOD, ElementType.CONSTRUCTOR })
	public @interface EnsureOnlys {
		EnsureOnly[] value();
	}

	@Repeatable(Raises.class)
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.METHOD, ElementType.CONSTRUCTOR })
	public @interface Raise {
		Class<?> exception() default Throwable.class;

		String when() default "[unassigned]";
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.METHOD, ElementType.CONSTRUCTOR })
	public @interface Raises {
		Raise[] value();
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.METHOD, ElementType.CONSTRUCTOR })
	public @interface Return {
		String when() default "[unassigned]";
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.METHOD, ElementType.CONSTRUCTOR })
	public @interface Returns {
		Return[] value();
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.METHOD })
	public @interface Ignore {
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.METHOD, ElementType.CONSTRUCTOR })
	public @interface Abstract {
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.METHOD })
	public @interface Pure {
	}

	public static void assertion(boolean behavior) {
	}

	public static void assumption(boolean behavior) {
	}

	public static void invariant(boolean behavior) {
	}

	public static Throwable thrown() {
		return null;
	}

}
