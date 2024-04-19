package byteback.syntax.scene.type.declaration.member.method.body.value.encoder.to_bpl;

import byteback.syntax.printer.Printer;

public class OldValueToBplEncoder extends ValueToBplEncoder {

	public static final String OLD_HEAP_SYMBOL = "heap'";

	public OldValueToBplEncoder(final Printer printer) {
		super(printer);
	}

	@Override
	public String getHeapSymbol() {
		return OLD_HEAP_SYMBOL;
	}

}
