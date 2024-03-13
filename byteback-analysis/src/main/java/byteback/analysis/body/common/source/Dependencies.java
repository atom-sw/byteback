package byteback.analysis.body.common.source;

import byteback.analysis.model.syntax.type.Type;

import java.util.HashSet;
import java.util.Set;

public class Dependencies {
    public final Set<Type> typesToHierarchy, typesToSignature;

    public Dependencies() {
        typesToHierarchy = new HashSet<>();
        typesToSignature = new HashSet<>();
    }

    public Dependencies(final Set<Type> typesToHierarchy, final Set<Type> typesToSignature) {
        this.typesToHierarchy = typesToHierarchy;
        this.typesToSignature = typesToSignature;
    }

}
