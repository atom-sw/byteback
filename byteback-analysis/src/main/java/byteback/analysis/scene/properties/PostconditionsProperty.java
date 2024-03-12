package byteback.analysis.scene.properties;

import byteback.analysis.common.Hosts;
import byteback.analysis.scene.Annotations;
import byteback.common.function.Lazy;
import soot.Scene;
import soot.SootMethod;
import soot.Value;

import java.util.Set;

public class PostconditionsProperty extends ConditionsProperty {

    private static final Lazy<PostconditionsProperty> instance = Lazy.from(PostconditionsProperty::new);

    private static final Scene scene = Scene.v();


    public static PostconditionsProperty v() {
        return instance.get();
    }

    protected static Set<Value> scanPostconditions(final SootMethod method) {
        Hosts.v().getAnnotations(method)
                .forEach(annotationTag ->
                        Annotations.v().getAnnotations(annotationTag)
                                .forEach((subTag) -> {
                                }));

        return null;
    }

    @Override
    public void collect(final SootMethod traceMethod, final Set<Value> upperConditions) {
    }

}
