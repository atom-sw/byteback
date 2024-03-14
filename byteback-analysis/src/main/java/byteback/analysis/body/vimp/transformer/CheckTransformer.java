package byteback.analysis.body.vimp.transformer;

import byteback.analysis.body.common.syntax.Body;
import byteback.analysis.body.jimple.syntax.expr.*;
import byteback.analysis.body.common.syntax.stmt.Unit;
import byteback.analysis.body.common.syntax.expr.Value;
import byteback.analysis.body.common.transformer.BodyTransformer;
import byteback.analysis.body.jimple.syntax.stmt.AssignStmt;
import byteback.analysis.body.jimple.syntax.stmt.IfStmt;
import byteback.analysis.body.jimple.syntax.stmt.InvokeStmt;
import byteback.analysis.body.jimple.syntax.stmt.ThrowStmt;
import byteback.analysis.body.vimp.VimpExprFactory;
import byteback.analysis.common.syntax.Chain;
import byteback.analysis.common.syntax.HashChain;
import byteback.analysis.model.syntax.signature.MethodSignature;
import byteback.analysis.model.syntax.type.ClassType;
import byteback.analysis.model.syntax.type.VoidType;

import java.util.Collections;
import java.util.Iterator;
import java.util.Optional;
import java.util.function.Supplier;

public abstract class CheckTransformer extends BodyTransformer {

    public final ClassType exceptionType;

    public CheckTransformer(final ClassType exceptionType) {
        this.exceptionType = exceptionType;
    }

    public abstract Optional<Value> makeUnitCheck(final VimpExprFactory builder, final Unit unit);

    public Chain<Unit> makeThrowUnits(final Supplier<Local> exceptionLocalSupplier) {
        final Chain<Unit> units = new HashChain<>();
        final Local local = exceptionLocalSupplier.get();
        final Unit initUnit = new AssignStmt(local, new NewExpr(exceptionType));
        units.addLast(initUnit);
        final var constructorSignature =
                new MethodSignature("<init>", VoidType.v(), Collections.emptyList(), exceptionType);
        final SpecialInvokeExpr invokeExpr = new SpecialInvokeExpr(constructorSignature, local, Collections.emptyList());
        final Unit constructorUnit = new InvokeStmt(invokeExpr);
        units.addLast(constructorUnit);
        final Unit throwUnit = new ThrowStmt(local);
        units.addLast(throwUnit);

        return units;
    }

    @Override
    public void transformBody(final Body body) {
        final Chain<Unit> units = body.getUnits();
        final Iterator<Unit> unitIterator = body.getUnits().snapshotIterator();
        final LocalGenerator localGenerator = new SimpleLocalGenerator(body);
        final Supplier<Local> exceptionLocalSupplier = () -> localGenerator.generateLocal(exceptionType);
        final VimpExprFactory builder = new VimpExprFactory(localGenerator);

        while (unitIterator.hasNext()) {
            final Unit unit = unitIterator.next();
            final Optional<Value> unitCheckOption = makeUnitCheck(builder, unit);

            if (unitCheckOption.isPresent()) {
                final Value unitCheck = unitCheckOption.get();
                final Chain<Unit> throwStmts = makeThrowUnits(exceptionLocalSupplier);
                units.insertBefore(throwStmts, unit);
                final Unit checkStmt = new IfStmt(unitCheck, unit);
                units.insertBefore(checkStmt, throwStmts.getFirst());
            }
        }
    }

}
