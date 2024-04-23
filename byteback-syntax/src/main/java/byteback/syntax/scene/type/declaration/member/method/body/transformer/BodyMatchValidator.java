package byteback.syntax.scene.type.declaration.member.method.body.transformer;

import byteback.syntax.scene.type.declaration.member.method.body.context.BodyContext;
import byteback.syntax.scene.type.declaration.member.method.body.unit.box.ImmutableUnitBox;
import byteback.syntax.scene.type.declaration.member.method.body.unit.context.UnitContext;
import byteback.syntax.scene.type.declaration.member.method.body.value.box.ImmutableValueBox;
import byteback.syntax.scene.type.declaration.member.method.body.value.context.ValueContext;
import byteback.syntax.transformer.TransformationException;
import soot.Body;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.jimple.Stmt;
import soot.jimple.internal.StmtBox;

public abstract class BodyMatchValidator extends BodyTransformer {

    public abstract boolean admitsDef(final ValueContext value);

    public abstract boolean admitsUse(final ValueContext value);

    public abstract boolean admitsUnit(final UnitContext unit);

    @Override
    public void transformBody(final BodyContext bodyContext) {
        final Body body = bodyContext.getBody();

        for (final Unit unit : body.getUnits()) {
            final var unitBox = new ImmutableUnitBox(unit);
            final var unitContext = new UnitContext(bodyContext, unitBox);

            if (!admitsUnit(unitContext)) {
                throw new TransformationException("Invalid statement: " + unit + ".", unit);
            }

            for (final ValueBox useBox : unit.getUseBoxes()) {
                final Value value = useBox.getValue();
                final var immutableUseBox = new ImmutableValueBox(value);
                final var valueContext = new ValueContext(unitContext, immutableUseBox);

                if (!admitsUse(valueContext)) {
                    throw new TransformationException("Invalid use expression: " + value + ".", unit);
                }
            }

            for (final ValueBox defBox : unit.getDefBoxes()) {
                final Value value = defBox.getValue();
                final var immutableUseBox = new ImmutableValueBox(value);
                final var valueContext = new ValueContext(unitContext, immutableUseBox);

                if (!admitsDef(valueContext)) {
                    throw new TransformationException("Invalid definition expression: " + value + ".", unit);
                }
            }
        }
    }

}
