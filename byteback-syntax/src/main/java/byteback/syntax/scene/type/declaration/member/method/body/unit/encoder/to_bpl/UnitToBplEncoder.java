package byteback.syntax.scene.type.declaration.member.method.body.unit.encoder.to_bpl;

import byteback.syntax.printer.Printer;
import byteback.syntax.scene.type.declaration.encoder.to_bpl.ClassToBplEncoder;
import byteback.syntax.scene.type.declaration.member.field.encoder.to_bpl.FieldToBplEncoder;
import byteback.syntax.scene.type.declaration.member.method.body.Vimp;
import byteback.syntax.scene.type.declaration.member.method.body.unit.AssertStmt;
import byteback.syntax.scene.type.declaration.member.method.body.unit.AssumeStmt;
import byteback.syntax.scene.type.declaration.member.method.body.unit.CallStmt;
import byteback.syntax.scene.type.declaration.member.method.body.unit.YieldStmt;
import byteback.syntax.scene.type.declaration.member.method.body.unit.encoder.UnitEncoder;
import byteback.syntax.scene.type.declaration.member.method.body.value.analyzer.VimpEffectEvaluator;
import byteback.syntax.scene.type.declaration.member.method.body.value.encoder.to_bpl.ValueToBplEncoder;
import byteback.syntax.scene.type.declaration.member.method.encoder.to_bpl.MethodToBplEncoder;
import soot.*;
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

	public void encodeInvoke(final InvokeExpr invokeExpr, final Value[] destValues) {
		printer.print("call ");
		printer.startItems(", ");

		for (final Value destValue : destValues) {
			printer.separate();
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
		}

		for (final Value argument : invokeExpr.getArgs()) {
			printer.separate();
			valueEncoder.encodeValue(argument);
		}

		printer.endItems();
		printer.print(");");
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

		if (unit instanceof final DefinitionStmt assignStmt) {
			final Value leftOp = assignStmt.getLeftOp();
			final Value rightOp = assignStmt.getRightOp();

			if (rightOp instanceof final NewArrayExpr newArrayExpr) {
				printer.print("call ");
				valueEncoder.encodeValue(leftOp);
				printer.print(", ");
				valueEncoder.encodeValue(Vimp.v().newThrownRef());
				printer.print(" := ");
				printer.print("array(");
				printer.startItems(", ");

				printer.separate();
				if (newArrayExpr.getBaseType() instanceof final RefType refType) {
					new ClassToBplEncoder(printer).encodeClassConstant(refType.getSootClass());
				} else {
					printer.print("Primitive");
				}

				printer.separate();
				valueEncoder.encodeValue(newArrayExpr.getSize());
				printer.endItems();
				printer.print(");");
				return;
			} else {
				if (leftOp instanceof final InstanceFieldRef instanceFieldRef) {
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
					return;
				} else if (leftOp instanceof final StaticFieldRef staticFieldRef) {
					printer.print(ValueToBplEncoder.HEAP_SYMBOL);
					printer.print(" := ");
					printer.print("store.update(");
					printer.startItems(", ");
					printer.separate();
					printer.print(ValueToBplEncoder.HEAP_SYMBOL);
					printer.separate();
					printer.print("type.reference(");
					final SootField sootField = staticFieldRef.getField();
					new ClassToBplEncoder(printer).encodeClassConstant(sootField.getDeclaringClass());
					printer.print(")");
					printer.separate();
					new FieldToBplEncoder(printer).encodeFieldConstant(staticFieldRef.getField());
					printer.separate();
					valueEncoder.encodeValue(rightOp);
					printer.endItems();
					printer.print(");");
					return;
				} else if (leftOp instanceof final ArrayRef arrayRef) {
					printer.print(ValueToBplEncoder.HEAP_SYMBOL);
					printer.print(" := ");
					printer.print("store.update(");
					printer.startItems(", ");
					printer.separate();
					printer.print(ValueToBplEncoder.HEAP_SYMBOL);
					printer.separate();
					valueEncoder.encodeValue(arrayRef.getBase());
					printer.separate();
					printer.print("array.element(");
					valueEncoder.encodeValue(arrayRef.getIndex());
					printer.print(")");
					printer.separate();
					printer.print("box(");
					valueEncoder.encodeValue(rightOp);
					printer.print(")");
					printer.endItems();
					printer.print(")");
					printer.print(";");
					return;
				} else {
					valueEncoder.encodeValue(assignStmt.getLeftOp());
					printer.print(" := ");
					valueEncoder.encodeValue(assignStmt.getRightOp());
					printer.print(";");
					return;
				}
			}
		}

		if (unit instanceof final CallStmt callStmt) {
			printer.print("call ");
			printer.startItems(", ");

			for (final Value target : callStmt.getTargets()) {
				printer.separate();
				new ValueToBplEncoder(printer).encodeValue(target);
			}

			printer.endItems();
			printer.print(" := ");

			new ValueToBplEncoder(printer).encodeValue(callStmt.getInvokeExpr());
			printer.print(";");
			return;
		}

		if (unit instanceof final InvokeStmt invokeStmt) {
			final InvokeExpr invokeExpr = invokeStmt.getInvokeExpr();
			encodeInvoke(invokeExpr, new Value[] { Vimp.v().newThrownRef() });
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
