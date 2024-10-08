package byteback.syntax.scene.type.declaration.member.field.encoder.to_bpl;

import byteback.syntax.printer.Printer;
import byteback.syntax.scene.type.declaration.member.field.encoder.FieldEncoder;
import byteback.syntax.scene.type.encoder.to_bpl.TypeAccessToBplEncoder;
import soot.SootClass;
import soot.SootField;

public class FieldToBplEncoder extends FieldEncoder {

	public FieldToBplEncoder(final Printer printer) {
		super(printer);
	}

	public void encodeFieldConstant(final SootField sootField) {
		final SootClass declaringClass = sootField.getDeclaringClass();
		printer.print("`");
		printer.print(declaringClass.getName());
		printer.print(".");
		printer.print(sootField.getName());
		printer.print("`");
	}

	public void encodeFieldConstantDeclaration(final SootField sootField) {
		printer.print("const unique ");
		encodeFieldConstant(sootField);
		printer.print(": Field ");
		new TypeAccessToBplEncoder(printer).encodeTypeAccess(sootField.getType());
		printer.print(";");
		printer.endLine();
	}

	public void encodeField(final SootField sootField) {
		encodeFieldConstantDeclaration(sootField);
	}

}
