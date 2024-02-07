package byteback.encoder.boogie;

import java.util.Optional;
import sootup.core.model.SootMethod;
import sootup.core.signatures.MethodSignature;
import sootup.core.types.Type;

public interface MethodSignatureToBplEncoder extends BplProcedureEncoder, BodyToBplEncoder {

	public default void encodeMethodIdentifier(final MethodSignature methodSignature) {
		encodeIdentifier(methodSignature.getName());
		encode("$");
		startSequence("#");
		for (final Type type : methodSignature.getParameterTypes()) {
			startItem();
			encodeTypeSignature(type);
		}
		startItem();
		encodeTypeSignature(methodSignature.getType());
		encode("$");
	}

	public default void encodeMethodSignature(final MethodSignature methodSignature) {
		final Optional<? extends SootMethod> sootMethodOptional = getView().getMethod(methodSignature);
		encodeProcedure();
		encodeMethodIdentifier(methodSignature);
		if (sootMethodOptional.isPresent()) {
			final SootMethod sootMethod = sootMethodOptional.get();
			if (sootMethod.hasBody()) {
				encodeBody(sootMethod.getBody());
			}
		} else {
			// TODO: Generate fake signature
			encode(BplSyntax.LINE_TERMINATOR);
		}
	}

}
