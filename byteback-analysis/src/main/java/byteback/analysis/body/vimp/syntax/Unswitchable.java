package byteback.analysis.body.vimp.syntax;

import soot.util.Switch;
import soot.util.Switchable;

public interface Unswitchable extends Switchable {

    default void apply(final Switch visitor) {
        throw new UnsupportedOperationException();
    }

}
