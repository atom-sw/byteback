package byteback.syntax.scene.type.declaration.member.method.body.value;

import soot.Type;
import soot.jimple.IdentityRef;
import soot.jimple.internal.JimpleLocal;

public abstract class ParameterLocal extends JimpleLocal implements IdentityRef {

    public ParameterLocal(final String name, final Type type) {
        super("@" + name, type);
    }

    @Override
    public Object clone() {
        throw new UnsupportedOperationException();
    }

}
