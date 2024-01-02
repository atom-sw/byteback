package byteback.analysis.transformer;

import byteback.analysis.tags.PositionTag;
import java.util.Map;
import soot.Body;
import soot.BodyTransformer;
import soot.Unit;
import soot.UnitBox;
import soot.grimp.GrimpBody;

public class PositionTagTransformer extends BodyTransformer implements UnitTransformer {

	public final String file;

	public PositionTagTransformer(final String file) {
		this.file = file;
	}

	@Override
	public void internalTransform(final Body body, final String phaseName, final Map<String, String> options) {
		if (body instanceof GrimpBody) {
			transformBody(body);
		} else {
			throw new IllegalArgumentException("Can only transform Grimp");
		}
	}

	@Override
	public void transformUnit(final UnitBox unitBox) {
		final Unit unit = unitBox.getUnit();
		final int lineNumber = unit.getJavaSourceStartLineNumber();
		unit.addTag(new PositionTag(file, lineNumber));
	}

}
