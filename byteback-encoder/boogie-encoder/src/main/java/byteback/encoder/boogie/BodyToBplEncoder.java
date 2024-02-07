package byteback.encoder.boogie;

import sootup.core.model.Body;
import sootup.core.signatures.MethodSignature;
import sootup.core.types.Type;
import sootup.core.types.VoidType;

public interface BodyToBplEncoder extends LocalToBplEncoder, StmtToBplEncoder {

	public default void encodeBody(final Body body) {
		final MethodSignature methodSignature = body.getMethodSignature();
		encode(BplSyntax.LPAREN);
		startSequence(", ");
		for (int i = 0; i < methodSignature.getParameterTypes().size(); ++i) {
			startItem();
			encodeLocalBinding(body.getParameterLocal(i));
		}
		endSequence();
		encode(BplSyntax.RPAREN);
		encodeSpace();
		encode(BplSyntax.RETURN);
		encodeSpace();
		encode(BplSyntax.LPAREN);
		final Type returnType = methodSignature.getType();
		startSequence(", ");
		if (!(returnType instanceof VoidType)) {
			startItem();
			encodeLocalBinding(BplConvention.RETURN_SYMBOL, returnType);
		}
		startItem();
		final Type exceptionType = getView().getProject().getIdentifierFactory().getClassType("java.lang.Exception");
		encodeLocalBinding(BplConvention.EXCEPTION_SYMBOL, exceptionType);
		endSequence();
		encode(BplSyntax.RPAREN);
		encodeNewline();
		encode(BplSyntax.LBRACE);
		encodeNewline();
		startSequence(";\n");
		encodeStmts(body.getStmts());
		endSequence();
		encode(BplSyntax.RBRACE);
		encodeNewline();
	}

}
