package byteback.syntax.scene.type.declaration.member.method.body.value.encoder.to_bpl;

import byteback.syntax.printer.Printer;

public class PostValueToBplEncoder extends ValueToBplEncoder {

	public PostValueToBplEncoder(final Printer printer) {
		super(printer);
	}

	@Override
	public String getOldHeapSymbol() {
		return "old(" + ValueToBplEncoder.HEAP_SYMBOL + ")";
	}

}
