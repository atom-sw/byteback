package byteback.analysis.transformer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import soot.Body;
import soot.BodyTransformer;
import soot.UnitBox;

public class ComposableTransformer extends BodyTransformer implements UnitTransformer {

	final List<UnitTransformer> unitTransformers;

	public ComposableTransformer() {
		this(new ArrayList<>());
	}

	public ComposableTransformer(final List<UnitTransformer> unitTransformers) {
		this.unitTransformers = unitTransformers;
	}

	public void addTransformer(final UnitTransformer transformer) {
		unitTransformers.add(transformer);
	}

	@Override
	protected void internalTransform(final Body body, String phaseName, Map<String, String> options) {
		transformBody(body);
	}

	@Override
	public void transformUnit(final UnitBox unitBox) {
		for (UnitTransformer transformer : unitTransformers) {
			transformer.transformUnit(unitBox);
		}
	}

}
