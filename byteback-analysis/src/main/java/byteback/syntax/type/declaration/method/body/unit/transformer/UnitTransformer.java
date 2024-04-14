package byteback.syntax.type.declaration.method.body.unit.transformer;

import byteback.syntax.type.declaration.method.body.context.BodyContext;
import byteback.syntax.type.declaration.method.body.unit.context.UnitContext;
import byteback.syntax.type.declaration.method.body.unit.walker.UnitWalker;
import soot.*;

/**
 * Body transformer that applies a transformation to each unit.
 *
 * @author paganma
 */
public abstract class UnitTransformer extends UnitWalker<BodyContext, UnitContext> {

    @Override
    public BodyContext makeBodyContext(final Body body) {
        return new BodyContext(body);
    }

    @Override
    public UnitContext makeUnitContext(final BodyContext bodyContext, final UnitBox unitBox) {
        return new UnitContext(bodyContext, unitBox);
    }

}
