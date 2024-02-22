package byteback.converter.soottoboogie.method.procedure;

import byteback.analysis.common.namespace.BBLibNamespace;
import byteback.analysis.model.SootAnnotationElems;
import byteback.analysis.model.SootAnnotations;
import byteback.converter.soottoboogie.Convention;
import byteback.converter.soottoboogie.Prelude;
import byteback.converter.soottoboogie.type.AbstractTypeReferenceExtractor;
import byteback.frontend.boogie.ast.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import soot.*;
import soot.Type;
import soot.tagkit.AnnotationElem;
import soot.tagkit.AnnotationTag;

public class RaiseConverter extends ConditionConverter {

	public RaiseConverter(final SootMethod target) {
		super(target);
	}

	public Optional<String> parseSourceName(final AnnotationTag tag) {
		return SootAnnotations.getElem(tag, "when").map((element) ->
				new SootAnnotationElems.StringElemExtractor().visit(element))
				.orElseThrow();
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

		final AnnotationElem exceptionElem = SootAnnotations.getElem(tag, "exception").orElseThrow();
		final String value = new SootAnnotationElems.ClassElemExtractor().visit(exceptionElem).orElseThrow();
		final RefType exceptionType = Scene.v().loadClassAndSupport(BBLibNamespace.stripDescriptor(value)).getType();
		final SymbolicReference typeReference = new AbstractTypeReferenceExtractor().visit(exceptionType);
		final FunctionReference rightExpression = Prelude.v().getInstanceOfFunction().makeFunctionReference();
		final ValueReference heapReference = Prelude.v().getHeapVariable().makeValueReference();
		rightExpression.addArgument(heapReference);
		rightExpression.addArgument(Convention.makeExceptionReference());
		rightExpression.addArgument(typeReference);
		final Expression condition = new ImplicationOperation(leftExpression, rightExpression);
		final var attributes = new byteback.frontend.boogie.ast.List<Attribute>();

		return new PostCondition(attributes, false, condition);
	}

}
