package byteback.analysis.scene.properties;

import byteback.analysis.common.Hosts;
import byteback.analysis.common.property.Properties;
import byteback.analysis.scene.Annotations;
import byteback.common.function.Lazy;
import soot.SootMethod;
import soot.Value;

import java.util.Set;

public class PreconditionsProperty extends Properties<SootMethod, Set<Value>> {

    private static final Lazy<PreconditionsProperty> instance = Lazy.from(PreconditionsProperty::new);

    public static PreconditionsProperty v() {
        return instance.get();
    }

    @Override
    protected Set<Value> compute(final SootMethod instance) {
        Hosts.v().getAnnotations(instance).forEach(annotationTag ->
                Annotations.v().getAnnotations(annotationTag).forEach(System.out::println));

        return null;
    }

}
