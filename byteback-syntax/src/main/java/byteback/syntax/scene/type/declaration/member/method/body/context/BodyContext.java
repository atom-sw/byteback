package byteback.syntax.scene.type.declaration.member.method.body.context;

import soot.Body;
import soot.Context;
import soot.SootClass;
import soot.SootMethod;

/**
 * A context surrounding a single Body.
 *
 * @author paganma
 */
public class BodyContext implements Context {

    private final Body body;

    /**
     * Constructs a new {@link BodyContext}.
     *
     * @param body The body within this context.
     */
    public BodyContext(final Body body) {
        this.body = body;
    }

    /**
     * Getter for the body within this Context.
     *
     * @return The body within this context.
     */
    public Body getBody() {
        return body;
    }

    /**
     * Getter for the outer method definition owning the body.
     *
     * @return The outer method definition.
     */
    public SootMethod getSootMethod() {
        return body.getMethod();
    }

    /**
     * Getter for the outer class definition declaring the method.
     *
     * @return The outer class definition.
     */
    public SootClass getSootClass() {
        return body.getMethod().getDeclaringClass();
    }

}
