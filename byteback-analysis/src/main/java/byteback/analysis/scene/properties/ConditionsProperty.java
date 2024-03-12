package byteback.analysis.scene.properties;

import byteback.analysis.common.property.Properties;
import soot.ClassModel;
import soot.SootMethod;
import soot.Value;
import soot.util.Chain;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public abstract class ConditionsProperty extends Properties<SootMethod, Set<Value>> {

    public abstract void collect(final SootMethod traceMethod, final Set<Value> upperConditions);

    @Override
    protected Set<Value> compute(final SootMethod instance) {
        final ClassModel declaringClass = instance.getDeclaringClass();
        final var nextClasses = new ArrayDeque<ClassModel>();
        final var currentTrace = new ArrayDeque<ClassModel>();
        final var visitedClasses = new HashSet<>();

        nextClasses.add(declaringClass);

        while (!nextClasses.isEmpty()) {
            final ClassModel currentClass = nextClasses.pop();
            visitedClasses.add(currentClass);
            currentTrace.add(currentClass);

            final ClassModel superClass = currentClass.getSuperclassUnsafe();
            final Chain<ClassModel> directSuperInterfaces = currentClass.getInterfaces();
            boolean hasUnvisitedParents = false;

            if (superClass != null && !visitedClasses.contains(superClass)) {
                nextClasses.add(superClass);
                hasUnvisitedParents = true;
            }

            for (final ClassModel superInterface : directSuperInterfaces) {
                if (!visitedClasses.contains(superInterface)) {
                    nextClasses.add(superInterface);
                    hasUnvisitedParents = true;
                }
            }

            if (!hasUnvisitedParents) {
                final var conditionsSet = new HashSet<Value>();
                final Iterator<ClassModel> traceIterator = currentTrace.descendingIterator();

                while (traceIterator.hasNext()) {
                    final ClassModel traceClass = traceIterator.next();
                    final SootMethod traceMethod = traceClass.getMethodUnsafe(instance.getSubSignature());
                    collect(traceMethod, conditionsSet);
                    final Set<Value> traceConditionSet = get(traceMethod).orElseGet(HashSet::new);
                    traceConditionSet.addAll(conditionsSet);
                    set(traceMethod, traceConditionSet);
                }

                currentTrace.pop();
            }
        }

        return get(instance).orElseThrow();
    }

}
