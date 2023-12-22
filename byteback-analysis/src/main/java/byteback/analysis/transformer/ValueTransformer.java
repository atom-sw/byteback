package byteback.analysis.transformer;

import soot.Body;
import soot.Unit;
import soot.UnitBox;
import soot.ValueBox;

public interface ValueTransformer extends UnitTransformer {

	void transformValue(ValueBox vbox);

	default void transformBody(final Body body) {
		for (final ValueBox vbox : body.getUseBoxes()) {
			transformValue(vbox);
		}
	}

	@Override
	default void transformUnit(final UnitBox unitBox) {
		final Unit unit = unitBox.getUnit();

		for (final ValueBox useBox : unit.getUseBoxes()) {
			transformValue(useBox);
		}
	}

}
