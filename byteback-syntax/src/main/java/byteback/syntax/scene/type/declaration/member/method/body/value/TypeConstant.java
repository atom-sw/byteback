package byteback.syntax.scene.type.declaration.member.method.body.value;

import byteback.syntax.scene.type.TypeType;
import soot.RefType;
import soot.Type;
import soot.UnitPrinter;
import soot.jimple.Constant;

public class TypeConstant extends Constant implements DefaultCaseValue {

	public final RefType value;

	public TypeConstant(final RefType type) {
		this.value = type;
	}

	@Override
	public Type getType() {
		return TypeType.v();
	}

	@Override
	public Object clone() {
		return new TypeConstant(value);
	}

	@Override
	public boolean equivTo(final Object object) {
		return object instanceof final TypeConstant typeConstant
				&& typeConstant.value == value;
	}

	@Override
	public int equivHashCode() {
		return value.getNumber() * 31;
	}

	@Override
	public void toString(final UnitPrinter printer) {
		printer.literal("@" + value.toString());
	}

	@Override
	public String toString() {
		return "@" + value.toString();
	}

}
