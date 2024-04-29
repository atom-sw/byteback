package byteback.syntax.scene.type.declaration.member.method.body.value;

import byteback.common.function.Lazy;
import byteback.syntax.scene.type.declaration.member.method.body.transformer.GuardTransformer;
import soot.RefType;
import soot.Type;
import soot.jimple.Jimple;
import soot.jimple.internal.JimpleLocal;
import soot.util.Switch;

/**
 * A concrete version of JCaughtExceptionRef, which can be assigned to. We use this to model exceptional behavior using
 * branches/guards.
 *
 * @author paganma
 * @see GuardTransformer
 */
public class ThrownRef extends OutputRef implements DefaultCaseValue {

    public ThrownRef() {
        super("thrown", RefType.v("java.lang.Throwable"));
    }

    @Override
    public boolean equivTo(final Object object) {
        return object instanceof ThrownRef;
    }

    @Override
    public int equivHashCode() {
        return 34949;
    }

}
