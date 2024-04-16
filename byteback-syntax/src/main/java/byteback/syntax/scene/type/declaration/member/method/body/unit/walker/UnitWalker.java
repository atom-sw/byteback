package byteback.syntax.scene.type.declaration.member.method.body.unit.walker;

import byteback.syntax.scene.type.declaration.member.method.body.context.BodyContext;
import byteback.syntax.scene.type.declaration.member.method.body.unit.context.UnitContext;
import byteback.syntax.scene.type.declaration.member.method.body.walker.BodyWalker;
import byteback.syntax.scene.type.declaration.member.method.body.unit.box.MutableUnitBox;
import soot.Body;
import soot.PatchingChain;
import soot.Unit;
import soot.UnitBox;

import java.util.Iterator;

public abstract class UnitWalker<B extends BodyContext, U extends UnitContext>
        extends BodyWalker<B> {

    public abstract U makeUnitContext(final B bodyContext, final UnitBox unitBox);

    public abstract void walkUnit(final U localUnitContext);

    @Override
    public void walkBody(final B bodyContext) {
        final Body body = bodyContext.getBody();
        final PatchingChain<Unit> units = body.getUnits();
        final Iterator<Unit> unitIterator = units.snapshotIterator();

        while (unitIterator.hasNext()) {
            final Unit unit = unitIterator.next();
            final U unitContext = makeUnitContext(bodyContext, new MutableUnitBox(unit, body));
            walkUnit(unitContext);
        }
    }

}
