package byteback.syntax.type.declaration.method.body.transformer;

import byteback.syntax.type.declaration.method.body.context.BodyContext;
import byteback.syntax.value.NestedExprConstructor;

import java.util.Collections;
import java.util.Iterator;
import java.util.Optional;

import byteback.syntax.Vimp;
import soot.*;
import soot.jimple.Jimple;
import soot.jimple.NewExpr;
import soot.jimple.SpecialInvokeExpr;
import soot.util.Chain;
import soot.util.HashChain;

/**
 * Transformer adding explicit checks for implicit exceptional behavior. The resulting effect of this transformation is:
 * If before executing a statement certain conditions for exceptional behavior hold, throw an exception.
 *
 * @author paganma
 */
public abstract class CheckTransformer extends BodyTransformer {

    public final String exceptionClassName;

    public CheckTransformer(final String exceptionClassName) {
        this.exceptionClassName = exceptionClassName;
    }

    public abstract Optional<Value> makeUnitCheck(final NestedExprConstructor builder, final Unit unit);



    public RefType getExceptionType() {
        assert Scene.v().doneResolving();

        return Scene.v().getRefType(exceptionClassName);
    }

    public SpecialInvokeExpr makeExceptionConstructionExpr(final SootClass exceptionClass, final Local local) {
        final SootMethodRef exceptionConstructorRef = Scene.v().makeConstructorRef(
                exceptionClass,
                Collections.emptyList()
        );
        return Jimple.v().newSpecialInvokeExpr(
                local,
                exceptionConstructorRef,
                Collections.emptyList()
        );
    }

    public Chain<Unit> makeThrowUnits(final LocalGenerator localGenerator) {
        final Chain<Unit> units = new HashChain<>();

        // Create new local $e for containing the exception
        final RefType exceptionType = getExceptionType();
        final Local local = localGenerator.generateLocal(exceptionType);

        // $e = new Exception;
        final NewExpr newExceptionExpr = Jimple.v().newNewExpr(exceptionType);
        final Unit assignNewExprUnit = Jimple.v().newAssignStmt(local, newExceptionExpr);

        // specialinvoke Exception($e);
        final SootClass exceptionClass = exceptionType.getSootClass();
        final SpecialInvokeExpr exceptionConstructionExpr = makeExceptionConstructionExpr(exceptionClass, local);
        final Unit exceptionConstructionUnit = Jimple.v().newInvokeStmt(exceptionConstructionExpr);

        // throw $e;
        final Unit throwUnit = Jimple.v().newThrowStmt(local);

        units.addLast(assignNewExprUnit);
        units.addLast(exceptionConstructionUnit);
        units.addLast(throwUnit);


        return units;
    }

    @Override
    public void walkBody(final BodyContext bodyContext) {
        final Body body = bodyContext.getBody();
        final Chain<Unit> units = body.getUnits();
        final Iterator<Unit> unitIterator = units.snapshotIterator();
        final LocalGenerator localGenerator = Scene.v().createLocalGenerator(body);
        final var immediateConstructor = new NestedExprConstructor(localGenerator);

        while (unitIterator.hasNext()) {
            final Unit unit = unitIterator.next();
            final Optional<Value> unitCheckOption = makeUnitCheck(immediateConstructor, unit);

            if (unitCheckOption.isPresent()) {
                final Value unitCheck = unitCheckOption.get();
                final Chain<Unit> throwUnits = makeThrowUnits(localGenerator);
                units.insertBefore(throwUnits, unit);
                final Unit checkStmt = Vimp.v().newIfStmt(unitCheck, unit);
                units.insertBefore(checkStmt, throwUnits.getFirst());
            }
        }
    }

}
