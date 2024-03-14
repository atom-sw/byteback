package byteback.analysis.body.common.syntax.stmt;

public class StmtBox extends UnitBox {

    public StmtBox(final Unit unit) {
        setUnit(unit);
    }

    @Override
    public boolean canContainUnit(final Unit unit) {
        return unit == null || unit instanceof Stmt;
    }
}
