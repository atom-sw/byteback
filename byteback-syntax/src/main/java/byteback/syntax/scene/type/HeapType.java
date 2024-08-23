package byteback.syntax.scene.type;

import byteback.common.function.Lazy;
import soot.Type;

/**
 * A type for a heap reference.
 *
 * @author paganma
 */
public class HeapType extends Type implements DefaultCaseType {

	private static final Lazy<HeapType> INSTANCE = Lazy.from(HeapType::new);

	public static HeapType v() {
		return INSTANCE.get();
	}

	private HeapType() {
	}

	@Override
	public String toString() {
		return "Heap";
	}

}
