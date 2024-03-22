package byteback.analysis.body.vimp.syntax;

import soot.Immediate;
import soot.jimple.ConcreteRef;
import soot.jimple.internal.JCaughtExceptionRef;

/**
 * A concrete version of JCaughtExceptionRef, which can be assigned to. We use this to model exceptional behavior using
 * branches/guards
 * @see byteback.analysis.body.vimp.transformer.GuardTransformer
 * @author paganma
 */
public class ConcreteCaughtExceptionRef extends JCaughtExceptionRef implements ConcreteRef, Immediate {
}
