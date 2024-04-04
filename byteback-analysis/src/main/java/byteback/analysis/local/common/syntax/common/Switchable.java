package byteback.analysis.local.common.syntax.common;

import soot.util.Switch;

/**
 * An instance which may be visited by a `Switch` visitor.
 *
 * @author paganma
 */
public interface Switchable {

    void apply(final Switch visitor);

}
