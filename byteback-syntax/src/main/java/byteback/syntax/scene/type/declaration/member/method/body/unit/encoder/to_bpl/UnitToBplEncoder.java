package byteback.syntax.scene.type.declaration.member.method.body.unit.encoder.to_bpl;

import byteback.syntax.printer.Printer;
import byteback.syntax.scene.type.declaration.encoder.to_bpl.ClassToBplEncoder;
import byteback.syntax.scene.type.declaration.member.field.encoder.to_bpl.FieldToBplEncoder;
import byteback.syntax.scene.type.declaration.member.method.body.Vimp;
import byteback.syntax.scene.type.declaration.member.method.body.unit.AssertStmt;
import byteback.syntax.scene.type.declaration.member.method.body.unit.AssumeStmt;
import byteback.syntax.scene.type.declaration.member.method.body.unit.YieldStmt;
import byteback.syntax.scene.type.declaration.member.method.body.unit.encoder.UnitEncoder;
import byteback.syntax.scene.type.declaration.member.method.body.value.analyzer.VimpEffectEvaluator;
import byteback.syntax.scene.type.declaration.member.method.body.value.analyzer.VimpTypeInterpreter;
import byteback.syntax.scene.type.declaration.member.method.body.value.encoder.to_bpl.ValueToBplEncoder;
import byteback.syntax.scene.type.declaration.member.method.encoder.to_bpl.MethodToBplEncoder;
import byteback.syntax.scene.type.encoder.to_bpl.TypeAccessToBplEncoder;
import soot.Local;
import soot.Unit;
import soot.Value;
import soot.jimple.*;

import java.util.Map;

public class UnitToBplEncoder extends UnitEncoder {

    private final Map<Unit, String> unitToLabelMap;

    private final ValueToBplEncoder valueEncoder;

    public UnitToBplEncoder(final Printer printer, final Map<Unit, String> unitToLabelMap) {
        super(printer);
        this.unitToLabelMap = unitToLabelMap;
        this.valueEncoder = new ValueToBplEncoder(printer);
    }

    public void encodeCall(final String procedureName, final Value[] destValues, final Value[] argValues) {
        printer.print("call ");
        printer.startItems(", ");

        for (final Value destValue : destValues) {
            printer.separate();
            valueEncoder.encodeValue(destValue);
        }

        printer.endItems();
        printer.print(" := ");
        printer.print(procedureName);
        printer.print("(");
        printer.startItems(", ");

        for (final Value argValue : argValues) {
            printer.separate();
            valueEncoder.encodeValue(argValue);
        }

        printer.print(")");
        printer.endItems();
        printer.print(";");
    }

    public void encodeInvoke(final InvokeExpr invokeExpr, final Value[] destValues) {
        printer.print("call ");
        printer.startItems(", ");

        for (final Value destValue : destValues) {
            valueEncoder.encodeValue(destValue);
        }

        printer.endItems();
        printer.print(" := ");

        new MethodToBplEncoder(printer).encodeMethodName(invokeExpr.getMethod());

        printer.print("(");
        printer.startItems(", ");

        if (invokeExpr instanceof final InstanceInvokeExpr instanceInvokeExpr) {
            printer.separate();
            valueEncoder.encodeValue(instanceInvokeExpr.getBase());
        } else if (invokeExpr instanceof final StaticInvokeExpr staticInvokeExpr) {
            throw new RuntimeException();
        }

        for (final Value argument : invokeExpr.getArgs()) {
            printer.separate();
            valueEncoder.encodeValue(argument);
        }

        printer.endItems();
        printer.print(")");

        printer.print(";");
    }

    @Override
    public void encodeUnit(final Unit unit) {
        if (unit instanceof final AssumeStmt assumeStmt) {
            printer.print("assume ");
            valueEncoder.encodeValue(assumeStmt.getCondition());
            printer.print(";");
            return;
        }

        if (unit instanceof final AssertStmt assertStmt) {
            printer.print("assert ");
            valueEncoder.encodeValue(assertStmt.getCondition());
            printer.print(";");
            return;
        }

        if (unit instanceof final AssignStmt assignStmt) {
            final Value leftOp = assignStmt.getLeftOp();
            final Value rightOp = assignStmt.getRightOp();

            if (rightOp instanceof final InvokeExpr invokeExpr && VimpEffectEvaluator.v().hasSideEffects(invokeExpr)) {
                if (leftOp instanceof final Local local) {
                    encodeInvoke(invokeExpr, new Value[] { local, Vimp.v().newCaughtExceptionRef() });
                    return;
                }
            } else if (rightOp instanceof final NewExpr newExpr) {
                printer.print("call ");
                valueEncoder.encodeValue(Vimp.v().newCaughtExceptionRef());
                printer.print(", ");
                valueEncoder.encodeValue(leftOp);
                printer.print(" := ");
                printer.print("new(");
                new ClassToBplEncoder(printer).encodeClassConstant(newExpr.getBaseType().getSootClass());
                printer.print(");");
                return;
            } else if (rightOp instanceof NewArrayExpr) {
              // TODO
              return;
            } else {
                if (leftOp instanceof Local) {
                    valueEncoder.encodeValue(assignStmt.getLeftOp());
                    printer.print(" := ");
                    valueEncoder.encodeValue(assignStmt.getRightOp());
                    printer.print(";");
                    return;
                } else if (leftOp instanceof final InstanceFieldRef instanceFieldRef) {
                    printer.print(ValueToBplEncoder.HEAP_SYMBOL);
                    printer.print(" := ");
                    printer.print("store.update(");
                    printer.startItems(", ");
                    printer.separate();
                    printer.print(ValueToBplEncoder.HEAP_SYMBOL);
                    printer.separate();
                    valueEncoder.encodeValue(instanceFieldRef.getBase());
                    printer.separate();
                    new FieldToBplEncoder(printer).encodeFieldConstant(instanceFieldRef.getField());
                    printer.separate();
                    valueEncoder.encodeValue(rightOp);
                    printer.endItems();
                    printer.print(");");
                }
            }
            return;
        }

        if (unit instanceof final InvokeStmt invokeStmt) {
            final InvokeExpr invokeExpr = invokeStmt.getInvokeExpr();
            encodeInvoke(invokeExpr, new Value[]{ Vimp.v().newCaughtExceptionRef() });
            return;
        }

        if (unit instanceof YieldStmt) {
            printer.print("return;");
            return;
        }

        if (unit instanceof final IfStmt ifStmt) {
            printer.print("if (");
            valueEncoder.encodeValue(ifStmt.getCondition());
            printer.print(") { goto ");
            printer.print(unitToLabelMap.get(ifStmt.getTarget()));
            printer.print("; }");
            return;
        }

        if (unit instanceof final GotoStmt gotoStmt) {
            printer.print("goto ");
            printer.print(unitToLabelMap.get(gotoStmt.getTarget()));
            printer.print(";");
            return;
        }

        throw new IllegalStateException("Unable to convert statement " + unit);
    }

}
