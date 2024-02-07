package byteback.encoder.boogie;

import sootup.core.jimple.basic.Local;
import sootup.core.types.Type;

public interface LocalToBplEncoder extends TypeToBplEncoder {

	default void encodeLocalIdentifier(final String name) {
		encode("$");
		encode(name);
	}

	default void encodeLocalBinding(final String name, final Type type) {
		encodeLocalIdentifier(name);
		encode(BplSyntax.TYPE_SEPARATOR);
		encodeTypeAccess(type);
	}

	default void encodeLocalBinding(final Local local) {
		encodeLocalBinding(local.getName(), local.getType());
	}

}
