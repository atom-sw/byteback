package byteback.analysis.scene.properties;

import byteback.analysis.common.Hosts;
import byteback.analysis.common.property.Properties;
import byteback.common.function.Lazy;
import soot.SootMethod;
import soot.Value;

import java.util.Set;

public class MethodPostconditions extends Properties<SootMethod, Set<Value>> {

    private static final Lazy<MethodPostconditions> instance = Lazy.from(MethodPostconditions::new);

    public static MethodPostconditions v() {
        return instance.get();
    }

    @Override
    protected Set<Value> compute(final SootMethod instance) {
        //Hosts.v().getAnnotations(instance);
    }

}
