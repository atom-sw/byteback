package soot;

import java.util.HashSet;
import java.util.Set;

public class Dependencies {
    public final Set<Type> typesToHierarchy, typesToSignature;

    public Dependencies() {
        typesToHierarchy = new HashSet<>();
        typesToSignature = new HashSet<>();
    }

    public Dependencies(Set<Type> typesToHierarchy, Set<Type> typesToSignature) {
        this.typesToHierarchy = typesToHierarchy == null ? new HashSet<>() : typesToHierarchy;
        this.typesToSignature = typesToSignature == null ? new HashSet<>() : typesToSignature;
    }
}
