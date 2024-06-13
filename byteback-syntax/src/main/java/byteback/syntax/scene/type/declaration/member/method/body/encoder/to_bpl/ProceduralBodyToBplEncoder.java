package byteback.syntax.scene.type.declaration.member.method.body.encoder.to_bpl;

import byteback.syntax.printer.Printer;
import byteback.syntax.scene.type.declaration.member.method.body.encoder.BodyEncoder;
import byteback.syntax.scene.type.declaration.member.method.body.unit.encoder.to_bpl.UnitToBplEncoder;
import byteback.syntax.scene.type.declaration.member.method.body.value.encoder.to_bpl.ValueToBplEncoder;
import byteback.syntax.scene.type.encoder.to_bpl.TypeAccessToBplEncoder;
import soot.*;
import soot.jimple.GotoStmt;
import soot.jimple.IfStmt;
import soot.jimple.Jimple;

import java.util.HashMap;

public class ProceduralBodyToBplEncoder extends BodyEncoder {

	public static final String STMT_INDENT = "  ";

	private final ValueToBplEncoder valueToBplEncoder;

	private final TypeAccessToBplEncoder typeAccessToBplEncoder;

	public ProceduralBodyToBplEncoder(final Printer printer) {
		super(printer);
		this.valueToBplEncoder = new ValueToBplEncoder(printer);
		this.typeAccessToBplEncoder = new TypeAccessToBplEncoder(printer);
	}

	public HashMap<Unit, String> makeUnitToLabelMap(final Body body) {
		final var unitToLabel = new HashMap<Unit, String>();
		int labelCounter = 0;

		for (final Unit unit : body.getUnits()) {
			final Unit target;

			if (unit instanceof final IfStmt ifStmt) {
				target = ifStmt.getTarget();
			} else if (unit instanceof final GotoStmt gotoStmt) {
				target = gotoStmt.getTarget();
			} else {
				continue;
			}

			unitToLabel.put(target, "L" + labelCounter++);
		}

		return unitToLabel;
	}

	public void encodeWhereClause(final Local local, final RefLikeType refType) {
		final Value whereValue = Jimple.v().newInstanceOfExpr(local, refType);
		printer.print(" where ");
		new ValueToBplEncoder(printer).encodeValue(whereValue);
	}

	@Override
	public void encodeBody(final Body body) {
		final HashMap<Unit, String> unitToLabelMap = makeUnitToLabelMap(body);
		final var unitToBplEncoder = new UnitToBplEncoder(printer, unitToLabelMap);

		for (final Local local : body.getLocals()) {
			printer.print(STMT_INDENT);
			printer.print("var ");
			valueToBplEncoder.encodeLocal(local);
			printer.print(": ");
			final Type localType = local.getType();
			typeAccessToBplEncoder.encodeTypeAccess(localType);

			if (localType instanceof final RefLikeType refType) {
				encodeWhereClause(local, refType);
			}

			printer.print(";");
			printer.endLine();
		}

		for (final Unit unit : body.getUnits()) {
			final String label = unitToLabelMap.get(unit);

			if (label != null) {
				printer.printLine(label + ":");
			}

			printer.print(STMT_INDENT);
			unitToBplEncoder.encodeUnit(unit);
			printer.endLine();
		}
	}

}
