package byteback.converter.soottoboogie.type;

import byteback.analysis.TypeSwitch;
import byteback.converter.soottoboogie.Prelude;
import byteback.frontend.boogie.ast.FunctionReference;
import byteback.frontend.boogie.ast.SymbolicReference;
import byteback.frontend.boogie.ast.ValueReference;
import soot.ArrayType;
import soot.RefType;

public class TypeReferenceExtractor extends TypeSwitch<SymbolicReference> {

	public SymbolicReference typeReference;

	@Override
	public void caseRefType(final RefType referenceType) {
		typeReference = ValueReference.of(ReferenceTypeConverter.typeName(referenceType.getSootClass()));
	}

	@Override
	public void caseArrayType(final ArrayType arrayType) {
		final FunctionReference arrayTypeReference = Prelude.v().getArrayTypeFunction().makeFunctionReference();
		SymbolicReference innerTypeReference = visit(arrayType.baseType);

		if (innerTypeReference == null) {
			innerTypeReference = Prelude.v().getPrimitiveTypeConstant().makeValueReference();
		}

		arrayTypeReference.addArgument(innerTypeReference);

		typeReference = arrayTypeReference;
	}

	public SymbolicReference result() {
		return typeReference;
	}

}
