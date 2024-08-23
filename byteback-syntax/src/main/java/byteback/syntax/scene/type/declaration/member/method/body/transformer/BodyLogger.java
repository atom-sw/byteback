package byteback.syntax.scene.type.declaration.member.method.body.transformer;

import soot.Body;

public class BodyLogger extends BodyTransformer {

	private final String methodName;

	public BodyLogger(final String methodName) {
		this.methodName = methodName;
	}

	public BodyLogger() {
		this.methodName = null;
	}

	@Override
	public void transformBody(final Body body) {
		if (methodName != null) {
			if (body.getMethod().getName().equals(methodName)) {
				System.out.println(body);
			}
		} else {
			System.out.println(body);
		}
	}

}
