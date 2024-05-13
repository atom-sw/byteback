package byteback.specification.plugin;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import byteback.specification.Contract.Prelude;

public class Plugin {

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.TYPE })
	public @interface Attach {
		String value();
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.FIELD })
	public @interface Export {
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.FIELD })
	public @interface Import {
	}

	@Prelude("plug")
	public static <T> T plug(final Class<T> type, final Object object) {
		throw new UnsupportedOperationException();
	}

}
