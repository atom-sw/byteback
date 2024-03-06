package byteback.analysis.scene.properties;

import byteback.analysis.common.Hosts;
import byteback.analysis.common.property.Properties;
import byteback.analysis.scene.Annotations;
import byteback.common.function.Lazy;
import soot.SootMethod;
import soot.Value;

import java.util.Set;

public class ClassInvariants extends Properties<SootMethod, Set<Value>> {

    private static final Lazy<ClassInvariants> instance = Lazy.from(ClassInvariants::new);

    public static ClassInvariants v() {
        return instance.get();
    }

    @Override
    protected Set<Value> compute(final SootMethod instance) {
        Hosts.v().getAnnotations(instance).forEach(annotationTag ->
                Annotations.v().getAnnotations(annotationTag).forEach(System.out::println));

        return null;
    }

}
