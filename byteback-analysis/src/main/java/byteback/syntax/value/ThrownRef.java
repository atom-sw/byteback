package byteback.syntax.value;

import byteback.syntax.type.declaration.method.body.transformer.GuardTransformer;
import soot.Immediate;
import soot.jimple.ConcreteRef;
import soot.jimple.internal.JCaughtExceptionRef;

/**
 * A concrete version of JCaughtExceptionRef, which can be assigned to. We use this to model exceptional behavior using
 * branches/guards.
 * @see GuardTransformer
 *
 * @author paganma
 */
public class ThrownRef extends JCaughtExceptionRef implements Immediate, ConcreteRef {
}
