package byteback.analysis.model.syntax.type.visitor;

import byteback.analysis.common.visitor.Visitor;
import byteback.analysis.model.syntax.type.*;

/**
 * Implements Switchable on base Java types.
 */
public class TypeSwitch<T> implements Visitor<Type, T> {

    T result;

    public void caseArrayType(final ArrayType arrayType) {
        defaultCase(arrayType);
    }

    public void caseBooleanType(final BooleanType booleanType) {
        defaultCase(booleanType);
    }

    public void caseByteType(final ByteType byteType) {
        defaultCase(byteType);
    }

    public void caseCharType(final CharType charType) {
        defaultCase(charType);
    }

    public void caseDoubleType(final DoubleType doubleType) {
        defaultCase(doubleType);
    }

    public void caseFloatType(final FloatType floatType) {
        defaultCase(floatType);
    }

    public void caseIntType(final IntType intType) {
        defaultCase(intType);
    }

    public void caseLongType(final LongType longType) {
        defaultCase(longType);
    }

    public void caseClassType(final ClassType classType) {
        defaultCase(classType);
    }

    public void caseShortType(final ShortType shortType) {
        defaultCase(shortType);
    }

    public void caseAddressType(final AddressType addressType) {
        defaultCase(addressType);
    }

    public void caseUnknownType(final UnknownType unknownType) {
        defaultCase(unknownType);
    }

    public void caseVoidType(final VoidType voidType) {
        defaultCase(voidType);
    }

    public void caseNullType(final NullType nullType) {
        defaultCase(nullType);
    }

    public void caseErroneousType(final ErroneousType erroneousType) {
        defaultCase(erroneousType);
    }

    public void setResult(T result) {
        this.result = result;
    }

    public T getResult() {
        return this.result;
    }

    public T visit(final Type type) {
        type.apply(this);

        return getResult();
    }

}
