package byteback.converter.soottoboogie.type;

import byteback.analysis.TypeSwitch;
import byteback.converter.soottoboogie.Prelude;
import byteback.frontend.boogie.ast.Expression;
import byteback.frontend.boogie.ast.FunctionReference;
import java.util.function.Function;
import soot.BooleanType;
import soot.IntType;
import soot.PrimType;
import soot.Type;

public class CasterProvider extends TypeSwitch<Function<Expression, Expression>> {

	private Function<Expression, Expression> caster;

	private final Type toType;

	public CasterProvider(Type toType) {
		if (toType != BooleanType.v()) {
			toType = Type.toMachineType(toType);
		}

		this.toType = toType;
	}

	public void setCaster(final Function<Expression, Expression> caster) {
		this.caster = caster;
	}

	@Override
	public Function<Expression, Expression> visit(Type fromType) {
		if (fromType != BooleanType.v()) {
			fromType = Type.toMachineType(fromType);
		}

		if (fromType == toType) {
			return Function.identity();
		} else {
			return super.visit(fromType);
		}
	}

	@Override
	public void caseBooleanType(final BooleanType fromType) {
		toType.apply(new TypeSwitch<>() {

			@Override
			public void caseIntType(final IntType toType) {
				setCaster((expression) -> {
					final FunctionReference casting = Prelude.v().getIntCastingFunction().makeFunctionReference();
					casting.addArgument(expression);

					return casting;
				});
			}

			@Override
			public void caseDefault(final Type toType) {
				throw new CastingModelException(fromType, toType);
			}

		});
	}

	@Override
	public void caseIntType(final IntType fromType) {
		toType.apply(new TypeSwitch<>() {

			@Override
			public void caseRealType(final PrimType type) {
				setCaster((expression) -> {
					final FunctionReference casting = Prelude.v().getIntToRealCastingFunction().makeFunctionReference();
					casting.addArgument(expression);

					return casting;
				});
			}

			@Override
			public void caseDefault(final Type toType) {
				CasterProvider.this.caseDefault(fromType);
			}

		});
	}

	@Override
	public void caseRealType(final PrimType fromType) {
		toType.apply(new TypeSwitch<>() {

			@Override
			public void caseIntType(final IntType type) {
				setCaster((expression) -> {
					final FunctionReference casting = Prelude.v().getRealToIntCastingFunction().makeFunctionReference();
					casting.addArgument(expression);

					return casting;
				});
			}

			@Override
			public void caseDefault(final Type toType) {
				CasterProvider.this.caseDefault(fromType);
			}

		});
	}

	@Override
	public void caseDefault(final Type fromType) {
		setCaster(Function.identity());
	}

	@Override
	public Function<Expression, Expression> result() {
		return caster;
	}

}
