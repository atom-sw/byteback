package byteback.syntax.type.declaration.method.body.context;

import soot.Body;
import soot.SootClass;
import soot.SootMethod;

public class BodyContext {

    private final Body body;

    public BodyContext(final Body body) {
        this.body = body;
    }

    public Body getBody() {
        return body;
    }

    public SootMethod getSootMethod() {
        return body.getMethod();
    }

    public SootClass getSootClass() {
        return body.getMethod().getDeclaringClass();
    }

}
