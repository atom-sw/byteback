package byteback.syntax.scene.type.declaration.member.method.body.unit.transformer;

import byteback.syntax.scene.type.declaration.member.method.body.transformer.context.BodyTransformationContext;
import byteback.syntax.scene.type.declaration.member.method.body.transformer.BodyTransformer;
import byteback.syntax.scene.type.declaration.member.method.body.unit.box.MutableUnitBox;
import byteback.syntax.scene.type.declaration.member.method.body.unit.transformer.context.UnitTransformationContext;
import soot.Body;
import soot.PatchingChain;
import soot.Unit;
import soot.UnitBox;

import java.util.Iterator;

public abstract class UnitTransformer extends BodyTransformer {

    public abstract void transformUnit(final UnitTransformationContext unitTransformationContext);

    @Override
    public void transformBody(final BodyTransformationContext bodyTransformationContext) {
        final Body body = bodyTransformationContext.getBody();
        final PatchingChain<Unit> units = body.getUnits();
        final Iterator<Unit> unitIterator = units.snapshotIterator();

        while (unitIterator.hasNext()) {
            final Unit unit = unitIterator.next();
            final UnitBox unitBox = new MutableUnitBox(unit, body);
            final var unitContext = new UnitTransformationContext(bodyTransformationContext, unitBox);
            transformUnit(unitContext);
        }
    }

}
