package byteback.syntax.scene.type.declaration.member.method.body.unit.transformer;

import byteback.syntax.scene.type.declaration.member.method.body.transformer.BodyTransformer;
import byteback.syntax.scene.type.declaration.member.method.body.unit.box.MutableUnitBox;
import soot.*;

import java.util.Iterator;

public abstract class UnitTransformer extends BodyTransformer {

	public abstract void transformUnit(final SootMethod sootMethod, final Body body, final UnitBox unitBox);

	@Override
	public void transformBody(final SootMethod sootMethod, final Body body) {
		final PatchingChain<Unit> units = body.getUnits();
		final Iterator<Unit> unitIterator = units.snapshotIterator();

		while (unitIterator.hasNext()) {
			final Unit unit = unitIterator.next();
			transformUnit(sootMethod, body, new MutableUnitBox(unit, body));
		}
	}

}
