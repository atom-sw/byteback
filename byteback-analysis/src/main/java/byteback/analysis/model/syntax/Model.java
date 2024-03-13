package byteback.analysis.model.syntax;

import byteback.analysis.model.syntax.signature.Signature;

public abstract class Model<T extends Signature> {

    private int modifiers;

    private T signature;

    public Model(final int modifiers, final T signature) {
        this.modifiers = modifiers;
        this.signature = signature;
    }

    public T getSignature() {
        return signature;
    }

    public void setSignature(final T signature) {
        this.signature = signature;
    }

    public int getModifiers() {
        return modifiers;
    }

    public void setModifiers(final int modifiers) {
        this.modifiers = modifiers;
    }
}
