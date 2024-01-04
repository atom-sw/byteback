package byteback.converter.soottoboogie.method.procedure;

import byteback.analysis.tags.PositionTag;
import byteback.analysis.util.AnnotationElems;
import byteback.analysis.util.SootAnnotations;
import byteback.converter.soottoboogie.Convention;
import byteback.frontend.boogie.ast.Attribute;
import byteback.frontend.boogie.ast.Condition;
import byteback.frontend.boogie.ast.Expression;
import byteback.frontend.boogie.ast.PostCondition;
import java.util.ArrayList;
import java.util.List;
import soot.Scene;
import soot.SootMethod;
import soot.Type;
import soot.VoidType;
import soot.tagkit.AnnotationElem;
import soot.tagkit.AnnotationTag;

public class EnsureConverter extends ConditionConverter {

	public EnsureConverter(final SootMethod target) {
		super(target);
	}

	public String parseSourceName(final AnnotationTag tag) {
		final AnnotationElem element = SootAnnotations.getValue(tag).get();

		return new AnnotationElems.StringElemExtractor().visit(element);
	}

	public Condition makeCondition(final String sourceName, final Expression expression) {
		final byteback.frontend.boogie.ast.List<Attribute> attributes = new byteback.frontend.boogie.ast.List<>();

		if (target.hasTag("PositionTag")) {
			final PositionTag tag = (PositionTag) target.getTag("PositionTag");
			final String message = tag.file + ": (line " + tag.lineNumber + "): Error: The postcondition " + sourceName
					+ " might not hold.";
			attributes.add(Convention.makeMessageAttribute(message));
		}

		return new PostCondition(attributes, false, expression);
	}

	public List<List<Type>> getSourceSignatures() {
		final List<List<Type>> signatures = new ArrayList<>();

		final List<Type> defaultSignature = new ArrayList<>(target.getParameterTypes());

		if (target.getReturnType() != VoidType.v()) {
			defaultSignature.add(target.getReturnType());
		}

		final List<Type> extendedSignature = new ArrayList<>(defaultSignature);
		extendedSignature.add(Scene.v().getType("java.lang.Throwable"));

		signatures.add(defaultSignature);
		signatures.add(extendedSignature);

		return signatures;
	}

	public Condition convert(final AnnotationTag tag) {
		final String sourceName = parseSourceName(tag);
		final Expression conditionExpression = convertSource(sourceName, getSourceSignatures());

		return makeCondition(sourceName, conditionExpression);
	}

}
