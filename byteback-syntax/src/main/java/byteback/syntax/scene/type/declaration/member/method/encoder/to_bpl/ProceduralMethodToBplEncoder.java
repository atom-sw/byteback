package byteback.syntax.scene.type.declaration.member.method.encoder.to_bpl;

import byteback.syntax.printer.Printer;
import byteback.syntax.scene.type.declaration.member.method.body.Vimp;
import byteback.syntax.scene.type.declaration.member.method.body.encoder.to_bpl.ProceduralBodyToBplEncoder;
import byteback.syntax.scene.type.declaration.member.method.body.tag.InferredFramesTagAccessor;
import byteback.syntax.scene.type.declaration.member.method.body.value.analyzer.VimpTypeInterpreter;
import byteback.syntax.scene.type.declaration.member.method.body.value.box.tag.FreeTagMarker;
import byteback.syntax.scene.type.declaration.member.method.body.value.encoder.to_bpl.ValueToBplEncoder;
import byteback.syntax.scene.type.declaration.member.method.analysis.ParameterRefFinder;
import byteback.syntax.scene.type.declaration.member.method.tag.PostassumptionsTagAccessor;
import byteback.syntax.scene.type.declaration.member.method.tag.PostconditionsTagAccessor;
import byteback.syntax.scene.type.declaration.member.method.tag.PreassumptionsTagAccessor;
import byteback.syntax.scene.type.declaration.member.method.tag.PreconditionsTagAccessor;
import byteback.syntax.scene.type.encoder.to_bpl.TypeAccessToBplEncoder;
import soot.*;
import soot.jimple.IdentityRef;

import java.util.List;

public class ProceduralMethodToBplEncoder extends MethodToBplEncoder {

	public static String SPEC_INDENT = "  ";

	public ProceduralMethodToBplEncoder(final Printer printer) {
		super(printer);
	}

	public void encodeInputRefs(final List<IdentityRef> inputRefs) {
		final var valueToBplEncoder = new ValueToBplEncoder(printer);
		final var typeAccessToBplEncoder = new TypeAccessToBplEncoder(printer);

		printer.print("(");
		printer.startItems(", ");

		for (final IdentityRef identityRef : inputRefs) {
			printer.separate();
			printer.endLine();
			printer.print(SPEC_INDENT);
			valueToBplEncoder.encodeInputRef(identityRef);
			printer.print(": ");
			final Type type = VimpTypeInterpreter.v().typeOf(identityRef);
			typeAccessToBplEncoder.encodeTypeAccess(type);

			if (type instanceof RefLikeType refLikeType) {
				new ProceduralBodyToBplEncoder(printer).encodeWhereClause(Vimp.v().nest(identityRef), refLikeType);
			}
		}

		printer.endLine();
		printer.endItems();
		printer.print(")");
	}

	public void encodeMethod(final SootMethod sootMethod) {
		final var typeAccessToBplEncoder = new TypeAccessToBplEncoder(printer);
		printer.print("procedure ");
		encodeMethodName(sootMethod);
		final List<IdentityRef> inputRefs = ParameterRefFinder.v().findInputRefs(sootMethod);
		encodeInputRefs(inputRefs);
		printer.print(" returns (");
		printer.startItems(", ");
		final Type returnType = sootMethod.getReturnType();
		final boolean isAbstract = sootMethod.isAbstract() || !sootMethod.hasActiveBody();

		if (returnType != VoidType.v()) {
			printer.separate();
			printer.print(ValueToBplEncoder.RETURN_SYMBOL + ": ");
			typeAccessToBplEncoder.encodeTypeAccess(returnType);
		}

		printer.separate();
		printer.print(ValueToBplEncoder.THROWN_SYMBOL + ": Reference");
		printer.print(")");
		printer.endItems();

		if (isAbstract) {
			printer.print(";");
		}

		printer.endLine();

		PreconditionsTagAccessor.v().get(sootMethod)
				.ifPresent((preconditionsTag) -> {
					final List<ValueBox> conditionBoxes = preconditionsTag.getConditionBoxes();

					for (final ValueBox valueBox : conditionBoxes) {
						final Value value = valueBox.getValue();
						printer.print(SPEC_INDENT);

						if (FreeTagMarker.v().hasTag(valueBox)) {
							printer.print("free ");
						}

						printer.print("requires ");
						new ValueToBplEncoder(printer).encodeValue(value);
						printer.printLine(";");
					}
				});

		PostconditionsTagAccessor.v().get(sootMethod)
				.ifPresent((postconditionsTag) -> {
					final List<ValueBox> conditionBoxes = postconditionsTag.getConditionBoxes();

					for (final ValueBox valueBox : conditionBoxes) {
						final Value value = valueBox.getValue();
						printer.print(SPEC_INDENT);

						if (FreeTagMarker.v().hasTag(valueBox)) {
							printer.print("free ");
						}

						printer.print("ensures ");
						new ValueToBplEncoder(printer).encodeValue(value);
						printer.printLine(";");
					}
				});

		PreassumptionsTagAccessor.v().get(sootMethod)
				.ifPresent((preassumptionsTag) -> {
					final List<ValueBox> conditionBoxes = preassumptionsTag.getConditionBoxes();

					for (final ValueBox valueBox : conditionBoxes) {
						final Value value = valueBox.getValue();
						printer.print(SPEC_INDENT);
						printer.print("free ");
						printer.print("requires ");
						new ValueToBplEncoder(printer).encodeValue(value);
						printer.printLine(";");
					}
				});

		PostassumptionsTagAccessor.v().get(sootMethod)
				.ifPresent((postassumptionsTag) -> {
					final List<ValueBox> conditionBoxes = postassumptionsTag.getConditionBoxes();

					for (final ValueBox valueBox : conditionBoxes) {
						final Value value = valueBox.getValue();
						printer.print(SPEC_INDENT);
						printer.print("free ");
						printer.print("ensures ");
						new ValueToBplEncoder(printer).encodeValue(value);
						printer.printLine(";");
					}
				});

		if (!isAbstract) {
			final var proceduralBodyToBplEncoder = new ProceduralBodyToBplEncoder(printer);
			final Body body = sootMethod.getActiveBody();

			if (InferredFramesTagAccessor.v().hasTag(body)) {
				printer.print(SPEC_INDENT);
				printer.printLine("modifies `#H`;");
				printer.print(SPEC_INDENT);
				printer.printLine("free ensures heap.succeeds(old(`#H`), `#H`);");
			}

			printer.printLine("{");
			proceduralBodyToBplEncoder.encodeBody(body);
			printer.printLine("}");
		}
	}

}
