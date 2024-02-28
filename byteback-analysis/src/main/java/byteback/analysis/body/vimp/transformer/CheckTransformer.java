package byteback.analysis.body.vimp.transformer;

import byteback.analysis.body.vimp.Vimp;
import byteback.common.Lazy;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import soot.*;
import soot.grimp.Grimp;
import soot.jimple.Jimple;
import soot.jimple.SpecialInvokeExpr;
import soot.util.Chain;
import soot.util.HashChain;

public abstract class CheckTransformer extends BodyTransformer {

    public final SootClass exceptionClass;

    public CheckTransformer(final SootClass exceptionClass) {
        this.exceptionClass = exceptionClass;
    }

    public Chain<Unit> createThrowUnits(final Supplier<Local> exceptionLocalSupplier) {
        final Chain<Unit> units = new HashChain<>();
        final Local local = exceptionLocalSupplier.get();
        final Unit initUnit = Grimp.v().newAssignStmt(local, Jimple.v().newNewExpr(exceptionClass.getType()));
        units.addLast(initUnit);
        final SootMethodRef constructorRef = exceptionClass.getMethod("<init>", Collections.emptyList()).makeRef();
        final SpecialInvokeExpr invokeExpr = Grimp.v().newSpecialInvokeExpr(local, constructorRef,
                Collections.emptyList());
        final Unit constructorUnit = Grimp.v().newInvokeStmt(invokeExpr);
        units.addLast(constructorUnit);
        final Unit throwUnit = Grimp.v().newThrowStmt(local);
        units.addLast(throwUnit);
        return units;
    }

    public abstract Optional<Value> createUnitCheck(final Unit unit);

    public void internalTransform(final Body body, final String phaseName, final Map<String, String> options) {
        final Chain<Unit> units = body.getUnits();
        final Iterator<Unit> unitIterator = body.getUnits().snapshotIterator();
        final LocalGenerator localGenerator = Scene.v().createLocalGenerator(body);
        final Lazy<Local> exceptionLocalSupplier = Lazy.from(() ->
                localGenerator.generateLocal(exceptionClass.getType()));

        while (unitIterator.hasNext()) {
            final Unit unit = unitIterator.next();
            final Optional<Value> unitCheckOption = createUnitCheck(unit);

            if (unitCheckOption.isPresent()) {
                final Value unitCheck = unitCheckOption.get();
                final Chain<Unit> throwStmts = createThrowUnits(exceptionLocalSupplier);
                units.insertBefore(throwStmts, unit);
                final Unit checkStmt = Vimp.v().newIfStmt(unitCheck, unit);
                units.insertBefore(checkStmt, throwStmts.getFirst());
            }
        }
    }

}
