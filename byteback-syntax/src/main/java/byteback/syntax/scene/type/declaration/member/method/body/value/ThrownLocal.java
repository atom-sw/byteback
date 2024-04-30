package byteback.syntax.scene.type.declaration.member.method.body.value;

import byteback.syntax.scene.type.declaration.member.method.body.transformer.GuardTransformer;
import soot.RefType;

/**
 * A concrete version of JCaughtExceptionRef, which can be assigned to. We use this to model exceptional behavior using
 * branches/guards.
 *
 * @author paganma
 * @see GuardTransformer
 */
public class ThrownLocal extends OutputLocal implements DefaultCaseValue {

    public ThrownLocal() {
        super("thrown", RefType.v("java.lang.Throwable"));
    }

    @Override
    public boolean equivTo(final Object object) {
        return object instanceof ThrownLocal;
    }

    @Override
    public int equivHashCode() {
        return 34949;
    }

}
