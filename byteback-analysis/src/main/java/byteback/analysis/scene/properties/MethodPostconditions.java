package byteback.analysis.scene.properties;

import byteback.analysis.common.Hosts;
import byteback.analysis.common.property.Properties;
import byteback.analysis.scene.AnnotationElems;
import byteback.analysis.scene.Annotations;
import byteback.common.function.Lazy;
import soot.*;
import soot.util.Chain;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MethodPostconditions extends Properties<SootMethod, Set<Value>> {

    private static final Lazy<MethodPostconditions> instance = Lazy.from(MethodPostconditions::new);

    private static final Scene scene = Scene.v();


    public static MethodPostconditions v() {
        return instance.get();
    }

    protected static Set<Value> scanPostconditions(final SootMethod method) {
        Hosts.v().getAnnotations(method)
                .forEach(annotationTag ->
                        Annotations.v().getAnnotations(annotationTag)
                                .forEach((subTag) -> {}));

        return null;
    }

    @Override
    protected Set<Value> compute(final SootMethod instance) {
        final SootClass declaringClass = instance.getDeclaringClass();
        final var nextClasses = new ArrayDeque<SootClass>();
        final var trace = new ArrayDeque<>();
        final var visitedClasses = new HashSet<>();

        nextClasses.add(declaringClass);

        while (!nextClasses.isEmpty()) {
            final SootClass currentClass = nextClasses.pop();
            visitedClasses.add(currentClass);
            trace.add(currentClass);

            final SootClass superClass = declaringClass.getSuperclass();
            final Chain<SootClass> directSuperInterfaces = declaringClass.getInterfaces();
            boolean hasUnvisitedParents = false;

            if (superClass != null && !visitedClasses.contains(superClass)) {
                nextClasses.add(superClass);
                hasUnvisitedParents = true;
            }

            for (final SootClass superInterface : directSuperInterfaces) {
                if (!visitedClasses.contains(superInterface)) {
                    nextClasses.add(superInterface);
                    hasUnvisitedParents = true;
                }
            }

            if (!hasUnvisitedParents) {
                while (!trace.isEmpty()) {
                    trace.pop();
                    // add the items
                }
            }

        }

        return null;
    }

}
