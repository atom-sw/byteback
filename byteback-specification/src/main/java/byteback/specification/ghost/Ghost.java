package byteback.specification.ghost;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public class Ghost {

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.TYPE })
	public @interface Attach {
		String value();
	}

	public static <T> T of(final Class<T> type, final Object object) {
		throw new UnsupportedOperationException();
	}

}
