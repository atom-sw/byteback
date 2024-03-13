package byteback.analysis.body.vimp.transformer;

import byteback.analysis.body.common.Body;
import byteback.analysis.body.common.syntax.Local;
import byteback.analysis.body.common.syntax.LocalGenerator;
import byteback.analysis.body.common.syntax.Value;
import byteback.analysis.body.common.transformer.BodyTransformer;
import byteback.analysis.body.jimple.syntax.Unit;
import byteback.analysis.body.vimp.Vimp;
import byteback.analysis.body.vimp.VimpExprFactory;
import byteback.analysis.model.syntax.ClassModel;
import soot.*;
import soot.grimp.Grimp;
import byteback.analysis.body.jimple.syntax.Jimple;
import byteback.analysis.body.jimple.syntax.SpecialInvokeExpr;
import soot.util.Chain;
import soot.util.HashChain;

import java.util.Collections;
import java.util.Iterator;
import java.util.Optional;
import java.util.function.Supplier;

public abstract class CheckTransformer extends BodyTransformer {

    public final ClassModel exceptionClass;

    public CheckTransformer(final ClassModel exceptionClass) {
        this.exceptionClass = exceptionClass;
    }

    public abstract Optional<Value> makeUnitCheck(final VimpExprFactory builder, final byteback.analysis.body.jimple.syntax.Unit unit);

    public Chain<byteback.analysis.body.jimple.syntax.Unit> makeThrowUnits(final Supplier<Local> exceptionLocalSupplier) {
        final Chain<byteback.analysis.body.jimple.syntax.Unit> units = new HashChain<>();
        final Local local = exceptionLocalSupplier.get();
        final byteback.analysis.body.jimple.syntax.Unit initUnit = Grimp.v().newAssignStmt(local, Jimple.v().newNewExpr(exceptionClass.getClassType()));
        units.addLast(initUnit);
        final SootMethodRef constructorRef = exceptionClass.getMethodModel("<init>", Collections.emptyList()).makeRef();
        final SpecialInvokeExpr invokeExpr = Grimp.v().newSpecialInvokeExpr(local, constructorRef,
                Collections.emptyList());
        final byteback.analysis.body.jimple.syntax.Unit constructorUnit = Grimp.v().newInvokeStmt(invokeExpr);
        units.addLast(constructorUnit);
        final byteback.analysis.body.jimple.syntax.Unit throwUnit = Grimp.v().newThrowStmt(local);
        units.addLast(throwUnit);

        return units;
    }

    @Override
    public void transformBody(final Body body) {
        final Chain<byteback.analysis.body.jimple.syntax.Unit> units = body.getUnits();
        final Iterator<byteback.analysis.body.jimple.syntax.Unit> unitIterator = body.getUnits().snapshotIterator();
        final LocalGenerator localGenerator = Scene.v().createLocalGenerator(body);
        final Supplier<Local> exceptionLocalSupplier = () ->
                localGenerator.generateLocal(exceptionClass.getClassType());
        final VimpExprFactory builder = new VimpExprFactory(localGenerator);

        while (unitIterator.hasNext()) {
            final byteback.analysis.body.jimple.syntax.Unit unit = unitIterator.next();
            final Optional<Value> unitCheckOption = makeUnitCheck(builder, unit);

            if (unitCheckOption.isPresent()) {
                final Value unitCheck = unitCheckOption.get();
                final Chain<byteback.analysis.body.jimple.syntax.Unit> throwStmts = makeThrowUnits(exceptionLocalSupplier);
                units.insertBefore(throwStmts, unit);
                final Unit checkStmt = Vimp.v().newIfStmt(unitCheck, unit);
                units.insertBefore(checkStmt, throwStmts.getFirst());
            }
        }
    }

}
