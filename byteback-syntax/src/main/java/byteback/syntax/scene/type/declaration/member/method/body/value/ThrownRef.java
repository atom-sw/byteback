package byteback.syntax.scene.type.declaration.member.method.body.value;

import byteback.syntax.scene.type.declaration.member.method.body.transformer.GuardTransformer;
import soot.Immediate;
import soot.jimple.ConcreteRef;
import soot.jimple.internal.JCaughtExceptionRef;

/**
 * A concrete version of JCaughtExceptionRef, which can be assigned to. We use this to model exceptional behavior using
 * branches/guards.
 *
 * @author paganma
 * @see GuardTransformer
 */
public class ThrownRef extends JCaughtExceptionRef implements Immediate, ConcreteRef {
}
