package byteback.syntax.scene.type.declaration.member.method.body.value;

import byteback.syntax.scene.type.TypeType;
import soot.RefLikeType;
import soot.Type;
import soot.UnitPrinter;
import soot.jimple.Constant;

public class TypeConstant extends Constant implements DefaultCaseValue {

	public final RefLikeType type;

	public TypeConstant(final RefLikeType type) {
		this.type = type;
	}

	@Override
	public Type getType() {
		return TypeType.v();
	}

	@Override
	public Object clone() {
		return new TypeConstant(type);
	}

	@Override
	public boolean equivTo(final Object object) {
		return object instanceof final TypeConstant typeConstant
				&& typeConstant.type == type;
	}

	@Override
	public int equivHashCode() {
		return type.getNumber() * 31;
	}

	@Override
	public void toString(final UnitPrinter printer) {
		printer.literal("@" + type.toString());
	}

	@Override
	public String toString() {
		return "@" + type.toString();
	}

}
