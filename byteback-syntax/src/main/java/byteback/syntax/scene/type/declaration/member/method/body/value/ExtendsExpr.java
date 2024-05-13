package byteback.syntax.scene.type.declaration.member.method.body.value;

import soot.BooleanType;
import soot.Type;
import soot.Value;
import soot.ValueBox;
import soot.jimple.BinopExpr;
import soot.jimple.Jimple;
import soot.jimple.internal.AbstractBinopExpr;

public class ExtendsExpr extends AbstractBinopExpr implements BinopExpr, DefaultCaseValue {

	public ExtendsExpr(final ValueBox op1Box, final ValueBox op2Box) {
		super(op1Box, op2Box);
	}

	@Override
	public String getSymbol() {
		return " <: ";
	}

	public ExtendsExpr(final Value op1, final Value op2) {
		this(Jimple.v().newImmediateBox(op1), Jimple.v().newImmediateBox(op2));
	}

	@Override
	public Type getType() {
		return BooleanType.v();
	}

	@Override
	public Object clone() {
		return new ExtendsExpr(op1Box, op2Box);
	}

	@Override
	public boolean equivTo(Object o) {
		return false;
	}

	@Override
	public int equivHashCode() {
		return (getOp1().equivHashCode() + getOp2().equivHashCode()) * 31;
	}

}
