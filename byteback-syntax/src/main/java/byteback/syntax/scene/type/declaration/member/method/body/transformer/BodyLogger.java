package byteback.syntax.scene.type.declaration.member.method.body.transformer;

import soot.Body;
import soot.SootMethod;

public class BodyLogger extends BodyTransformer {

	private final String methodName;

	public BodyLogger(final String methodName) {
		this.methodName = methodName;
	}

	public BodyLogger() {
		this.methodName = null;
	}

	@Override
	public void transformBody(final SootMethod sootMethod, final Body body) {
		if (methodName != null) {
			if (sootMethod.getName().equals(methodName)) {
				System.out.println(body);
			}
		} else {
			System.out.println(body);
		}
	}

}
