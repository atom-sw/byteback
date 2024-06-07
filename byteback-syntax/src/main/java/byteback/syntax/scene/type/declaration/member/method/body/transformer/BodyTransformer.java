package byteback.syntax.scene.type.declaration.member.method.body.transformer;

import byteback.syntax.transformer.Transformer;
import soot.Body;
import soot.SootMethod;

import java.util.Map;

public abstract class BodyTransformer extends soot.BodyTransformer implements Transformer {

	public abstract void transformBody(final SootMethod sootMethod, final Body body);

	@Override
	public void internalTransform(final Body body, final String phaseName, final Map<String, String> options) {
		transformBody(body.getMethod(), body);
	}

}
