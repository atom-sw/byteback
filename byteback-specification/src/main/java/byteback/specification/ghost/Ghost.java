package byteback.specification.ghost;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import byteback.specification.Contract.Prelude;

public class Ghost {

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.TYPE })
	public @interface Attach {
		String value();
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.FIELD })
	public @interface Export {
	}

	@Prelude("plug")
	public static <T> T of(final Class<T> type, final Object object) {
		throw new UnsupportedOperationException();
	}

}
