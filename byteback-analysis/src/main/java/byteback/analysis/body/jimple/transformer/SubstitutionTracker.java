package byteback.analysis.body.jimple.transformer;

import byteback.analysis.common.namespace.BBLibNamespace;
import byteback.common.Cons;
import byteback.common.SetHashMap;

import java.util.HashMap;
import java.util.Set;

import soot.Local;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.jimple.AssignStmt;
import soot.jimple.InvokeExpr;
import soot.jimple.NewArrayExpr;
import soot.jimple.NewExpr;
import soot.jimple.Ref;
import soot.jimple.toolkits.infoflow.CachedEquivalentValue;

public class SubstitutionTracker {

    public final HashMap<Local, Cons<AssignStmt, Value>> localToSubstitution;

    final SetHashMap<Value, Local> dependencyToLocals;

    public SubstitutionTracker() {
        this.localToSubstitution = new HashMap<>();
        this.dependencyToLocals = new SetHashMap<>();
    }

    public static boolean isPureInvocation(final InvokeExpr invokeValue) {
        final SootMethod method = invokeValue.getMethod();
        final SootClass clazz = method.getDeclaringClass();

        return BBLibNamespace.isPureMethod(method) || BBLibNamespace.isPredicateMethod(method)
                || BBLibNamespace.isSpecialClass(clazz) || BBLibNamespace.isQuantifierClass(clazz);
    }

    public static boolean hasSideEffects(final Value value) {
        return (value instanceof final InvokeExpr invokeExpr && !isPureInvocation(invokeExpr))
                || value instanceof NewExpr || value instanceof NewArrayExpr;
    }

    public void track(final AssignStmt assignStmt) {
        final Value leftValue = assignStmt.getLeftOp();
        final Value rightValue = assignStmt.getRightOp();

        if (leftValue instanceof final Local local) {
            if (!hasSideEffects(rightValue)) {
                localToSubstitution.put(local, new Cons<>(assignStmt, rightValue));
            }

            for (final ValueBox useBox : assignStmt.getUseBoxes()) {
                final Value useValue = useBox.getValue();

                if (useValue instanceof final Ref reference) {
                    dependencyToLocals.add(new CachedEquivalentValue(reference), local);
                }
            }
        } else if (leftValue instanceof final Ref reference) {
            final var cachedRef = new CachedEquivalentValue(reference);
            final Set<Local> dependentLocals = dependencyToLocals.get(cachedRef);

            if (dependentLocals != null) {
                for (final Local local : dependentLocals) {
                    localToSubstitution.remove(local);
                }
            }

            dependencyToLocals.remove(cachedRef);
        }

    }

    public void track(final Unit unit) {
        if (unit instanceof final AssignStmt assignUnit) {
            track(assignUnit);
        }
    }

    public Cons<AssignStmt, Value> substitute(final Local value) {
        return localToSubstitution.get(value);
    }

}
