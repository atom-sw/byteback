package byteback.analysis.body.vimp.transformer;

import byteback.analysis.body.common.transformer.BodyTransformer;
import byteback.analysis.body.vimp.ImmediateConstructor;

import java.util.Collections;
import java.util.Iterator;
import java.util.Optional;
import java.util.function.Supplier;

import byteback.analysis.body.vimp.Vimp;
import soot.*;
import soot.jimple.Jimple;
import soot.jimple.SpecialInvokeExpr;
import soot.util.Chain;
import soot.util.HashChain;

/**
 * Transformer adding explicit checks for implicit exceptional behavior. The resulting effect of this transformation is:
 * If before executing a statement certain conditions for exceptional behavior hold, throw an exception.
 * @author paganma
 */
public abstract class CheckTransformer extends BodyTransformer {

    public final String exceptionClassName;

    public CheckTransformer(final String exceptionClassName) {
        this.exceptionClassName = exceptionClassName;
    }

    public abstract Optional<Value> makeUnitCheck(final ImmediateConstructor builder, final Unit unit);

    public SootClass getExceptionClass() {
        assert Scene.v().doneResolving();

        return Scene.v().getSootClass(exceptionClassName);
    }

    public Chain<Unit> makeThrowUnits(final Supplier<Local> exceptionLocalSupplier) {
        final Chain<Unit> units = new HashChain<>();
        final Local local = exceptionLocalSupplier.get();
        final SootClass exceptionClass = getExceptionClass();
        final Unit initUnit = Jimple.v().newAssignStmt(local, Jimple.v().newNewExpr(exceptionClass.getType()));
        units.addLast(initUnit);
        // For now, we just assume that the constructor will be invoked without any argument.
        // TODO consider adding an option to generate the exception's constructor arguments in the subclasses of
        //  CheckTransformer.
        final SootMethodRef constructorRef = Scene.v().makeConstructorRef(exceptionClass, Collections.emptyList());
        final SpecialInvokeExpr invokeExpr = Jimple.v().newSpecialInvokeExpr(local, constructorRef,
                Collections.emptyList());
        final Unit constructorUnit = Jimple.v().newInvokeStmt(invokeExpr);
        units.addLast(constructorUnit);
        final Unit throwUnit = Jimple.v().newThrowStmt(local);
        units.addLast(throwUnit);

        return units;
    }

    @Override
    public void transformBody(final Body body) {
        final Chain<Unit> units = body.getUnits();
        final Iterator<Unit> unitIterator = body.getUnits().snapshotIterator();
        final LocalGenerator localGenerator = Scene.v().createLocalGenerator(body);
        final Supplier<Local> exceptionLocalSupplier = () ->
                localGenerator.generateLocal(getExceptionClass().getType());
        final ImmediateConstructor builder = new ImmediateConstructor(localGenerator);

        while (unitIterator.hasNext()) {
            final Unit unit = unitIterator.next();
            final Optional<Value> unitCheckOption = makeUnitCheck(builder, unit);

            if (unitCheckOption.isPresent()) {
                final Value unitCheck = unitCheckOption.get();
                final Chain<Unit> throwStmts = makeThrowUnits(exceptionLocalSupplier);
                units.insertBefore(throwStmts, unit);
                final Unit checkStmt = Vimp.v().newIfStmt(unitCheck, unit);
                units.insertBefore(checkStmt, throwStmts.getFirst());
            }
        }
    }

}
