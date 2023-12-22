package byteback.converter.soottoboogie.method.function;

import byteback.analysis.Namespace;
import byteback.converter.soottoboogie.ConversionException;
import byteback.converter.soottoboogie.expression.PureExpressionExtractor;
import byteback.converter.soottoboogie.type.TypeAccessExtractor;
import byteback.frontend.boogie.ast.ConditionalOperation;
import byteback.frontend.boogie.ast.Expression;
import byteback.frontend.boogie.ast.OldReference;
import byteback.frontend.boogie.ast.SetBinding;
import byteback.frontend.boogie.builder.SetBindingBuilder;
import java.util.Iterator;
import soot.Local;
import soot.SootClass;
import soot.SootMethod;
import soot.Value;

public class FunctionExpressionExtractor extends PureExpressionExtractor {

	@Override
	public FunctionExpressionExtractor makeExpressionExtractor() {
		return new FunctionExpressionExtractor();
	}

	public static SetBinding makeQuantifierBinding(final Local local) {
		final var bindingBuilder = new SetBindingBuilder();
		bindingBuilder.typeAccess(new TypeAccessExtractor().visit(local.getType()));
		bindingBuilder.name(PureExpressionExtractor.localName(local));

		return bindingBuilder.build();
	}

	public void pushOld(final SootMethod method, final Iterable<Value> arguments) {
		final Iterator<Value> argumentsIterator = arguments.iterator();
		setExpression(new OldReference(visit(argumentsIterator.next())));
		assert !argumentsIterator.hasNext() : "Wrong number of arguments to `old` reference";
	}

	public void pushConditional(final SootMethod method, final Iterable<Value> arguments) {
		final Iterator<Value> argumentsIterator = arguments.iterator();
		final Expression condition = visit(argumentsIterator.next());
		final Expression thenExpression = visit(argumentsIterator.next());
		final Expression elseExpression = visit(argumentsIterator.next());
		setExpression(new ConditionalOperation(condition, thenExpression, elseExpression));
		assert !argumentsIterator.hasNext() : "Wrong number of arguments to conditional expression";
	}

	public void pushSpecial(final SootMethod method, final Iterable<Value> arguments) {
		final String specialName = method.getName();

		if (specialName.equals(Namespace.OLD_NAME)) {
			pushOld(method, arguments);
		} else if (specialName.equals(Namespace.CONDITIONAL_NAME)) {
			pushConditional(method, arguments);
		} else {
			throw new ConversionException("Unknown special method: " + method.getName());
		}
	}

	public void pushBinding(final SootMethod method, final Iterable<Value> arguments) {
		throw new ConversionException("Cannot bind a free variable");
	}

	@Override
	public void setFunctionReference(final SootMethod method, final Iterable<Value> arguments) {
		final SootClass clazz = method.getDeclaringClass();
		if (Namespace.isBindingClass(clazz)) {
			pushBinding(method, arguments);
		} else if (Namespace.isSpecialClass(clazz)) {
			pushSpecial(method, arguments);
		} else {
			super.setFunctionReference(method, arguments);
		}
	}

}
