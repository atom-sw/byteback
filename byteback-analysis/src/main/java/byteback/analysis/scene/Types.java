package byteback.analysis.scene;

import byteback.common.function.Lazy;
import soot.BooleanType;
import soot.ByteType;
import soot.IntType;
import soot.LongType;
import soot.NullType;
import soot.Scene;
import soot.ShortType;
import soot.Type;
import soot.UnknownType;
import soot.VoidType;

/**
 * Utility class to work with Soot types.
 *
 * @author paganma
 */
public class Types {
	
	private static final Lazy<Types> instance = Lazy.from(Types::new);

	public static Types v() {
		return instance.get();
	}

	private Types() {
	}

}
