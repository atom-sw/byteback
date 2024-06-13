package byteback.syntax.scene.type.declaration.member.method.body.transformer;

import java.util.Optional;
import byteback.syntax.scene.type.declaration.member.method.body.Vimp;
import byteback.syntax.scene.type.declaration.member.method.body.value.LogicConstant;
import soot.LocalGenerator;
import soot.Unit;
import soot.Value;
import soot.util.Chain;
import soot.util.HashChain;

public class StrictCheckTransformer extends CheckTransformer {

	private final CheckTransformer checkTransformer;

	public StrictCheckTransformer(final CheckTransformer checkTransformer) {
		super(checkTransformer.getScene(), checkTransformer.getExceptionClassName());
		this.checkTransformer = checkTransformer;
	}

	public CheckTransformer getCheckTransformer() {
		return checkTransformer;
	}

	public Optional<Value> makeUnitCheck(final Unit unit) {
		return checkTransformer.makeUnitCheck(unit);
	}

	public Chain<Unit> makeHandlerUnits(final LocalGenerator localGenerator) {
		final Chain<Unit> units = new HashChain<>();
		units.add(Vimp.v().newAssertStmt(LogicConstant.v(false)));

		return units;
	}
	
}
