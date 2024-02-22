package byteback.converter.soottoboogie.method.procedure;

import byteback.analysis.model.SootAnnotationElems;
import byteback.analysis.model.SootAnnotations;
import byteback.frontend.boogie.ast.*;
import java.util.ArrayList;
import java.util.List;
import soot.SootMethod;
import soot.Type;
import soot.tagkit.AnnotationElem;
import soot.tagkit.AnnotationTag;

public class RequireConverter extends ConditionConverter {

	public RequireConverter(final SootMethod target) {
		super(target);
	}

	public String parseSourceName(final AnnotationTag tag) {
		final AnnotationElem element = SootAnnotations.getValue(tag).orElseThrow();

		return new SootAnnotationElems.StringElemExtractor().visit(element)
				.orElseThrow();
	}

	public Condition makeCondition(final String sourceName, final Expression expression) {
		final byteback.frontend.boogie.ast.List<Attribute> attributes = new byteback.frontend.boogie.ast.List<>();

		return new PreCondition(attributes, false, expression);
	}

	public List<List<Type>> getSourceSignatures() {
		final List<List<Type>> signatures = new ArrayList<>();
		final List<Type> defaultSignature = target.getParameterTypes();
		signatures.add(defaultSignature);

		return signatures;
	}

	public Condition convert(final AnnotationTag tag) {
		final String sourceName = parseSourceName(tag);
		final Expression conditionExpression = convertSource(sourceName, getSourceSignatures());

		return makeCondition(sourceName, conditionExpression);
	}

}
