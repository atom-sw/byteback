package byteback.converter.soottoboogie.method.procedure;

import byteback.analysis.model.SootAnnotationElems;
import byteback.analysis.model.SootAnnotations;
import byteback.converter.soottoboogie.Convention;
import byteback.converter.soottoboogie.Prelude;
import byteback.frontend.boogie.ast.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import soot.*;
import soot.Type;
import soot.tagkit.AnnotationTag;

public class ReturnConverter extends ConditionConverter {

	public ReturnConverter(final SootMethod target) {
		super(target);
	}

	public Optional<String> parseSourceName(final AnnotationTag tag) {
		return SootAnnotations.getElem(tag, "when").flatMap((element) ->
				new SootAnnotationElems.StringElemExtractor().visit(element));
	}

	public List<List<Type>> getSourceSignatures() {
		final List<List<Type>> signatures = new ArrayList<>();
		final List<Type> defaultSignature = target.getParameterTypes();
		signatures.add(defaultSignature);

		return signatures;
	}

	@Override
	public Condition convert(final AnnotationTag tag) {
		final Optional<String> sourceNameOptional = parseSourceName(tag);
		final Expression leftExpression;

		if (sourceNameOptional.isPresent()) {
			final String sourceName = sourceNameOptional.get();
			leftExpression = new OldReference(convertSource(sourceName, getSourceSignatures()));
		} else {
			leftExpression = BooleanLiteral.makeTrue();
		}

		final var rightExpression = new EqualsOperation(Convention.makeExceptionReference(),
				Prelude.v().getVoidConstant().makeValueReference());
		final Expression condition = new ImplicationOperation(leftExpression, rightExpression);
		final var attributes = new byteback.frontend.boogie.ast.List<Attribute>();

		return new PostCondition(attributes, false, condition);
	}

}
