package byteback.syntax.scene.type.declaration.member.method.body.transformer;

import byteback.syntax.scene.type.declaration.member.method.body.context.BodyContext;

public class BodyLogger extends BodyTransformer {

    private String methodName;

    public BodyLogger(final String methodName) {
        this.methodName = methodName;
    }

    public BodyLogger() {
        this.methodName = null;
    }

    @Override
    public void transformBody(final BodyContext bodyContext) {
        if (methodName != null) {
            if (bodyContext.getSootMethod().getName().equals(methodName)) {
                System.out.println(bodyContext.getBody());
            }
        } else {
            System.out.println(bodyContext.getBody());
        }
    }

}
