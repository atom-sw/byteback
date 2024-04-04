package byteback.analysis.local.vimp.syntax.value;

import byteback.analysis.local.vimp.transformer.body.GuardTransformer;
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
