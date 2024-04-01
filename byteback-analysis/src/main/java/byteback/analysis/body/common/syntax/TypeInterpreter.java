package byteback.analysis.body.common.syntax;

import soot.Type;
import soot.Value;

public interface TypeInterpreter<T extends Value> {

    Type typeOf(final T value);

}
