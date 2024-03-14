package byteback.analysis.body.common.syntax;

import java.util.List;

/**
 * An implementor of this interface indicates that it may contain UnitBoxes.
 *
 * <p>
 * Currently, this is implemented by soot.shimple.PhiExpr and used by soot.jimple.internal.JAssignStmt.
 *
 * @author Navindra Umanee
 */
public interface UnitBoxOwner {

    List<UnitBox> getUnitBoxes();

    void clearUnitBoxes();
}
