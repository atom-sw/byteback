package byteback.analysis.body.common.syntax;

import byteback.analysis.body.jimple.syntax.Unit;

import java.io.Serializable;

/**
 * A box which can contain units.
 *
 * @see byteback.analysis.body.jimple.syntax.Unit
 */
public interface UnitBox extends Serializable {

    void setUnit(Unit u);

    Unit getUnit();

    boolean canContainUnit(Unit u);

    boolean isBranchTarget();
}
