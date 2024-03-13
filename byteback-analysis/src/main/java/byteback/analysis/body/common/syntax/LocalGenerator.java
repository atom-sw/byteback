package byteback.analysis.body.common.syntax;

import byteback.analysis.model.syntax.type.Type;

public abstract class LocalGenerator {
    /**
     * generates a new soot local given the type
     */
    public abstract Local generateLocal(Type type);
}
