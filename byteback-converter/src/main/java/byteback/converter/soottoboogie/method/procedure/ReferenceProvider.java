package byteback.converter.soottoboogie.method.procedure;

import byteback.frontend.boogie.ast.ValueReference;
import soot.Type;

public interface ReferenceProvider {

	ValueReference get(final Type type);

}
