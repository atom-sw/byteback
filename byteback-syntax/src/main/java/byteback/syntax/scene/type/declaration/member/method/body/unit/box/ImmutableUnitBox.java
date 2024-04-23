package byteback.syntax.scene.type.declaration.member.method.body.unit.box;

import soot.AbstractUnitBox;
import soot.Unit;

public class ImmutableUnitBox extends AbstractUnitBox {

    public ImmutableUnitBox(final Unit unit) {
        setUnit(unit);
    }

    @Override
    public boolean canContainUnit(final Unit unit) {
        return true;
    }

}
