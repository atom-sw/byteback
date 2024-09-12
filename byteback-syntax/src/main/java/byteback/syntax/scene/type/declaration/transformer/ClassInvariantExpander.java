package byteback.syntax.scene.type.declaration.transformer;

import byteback.common.function.Lazy;
import byteback.syntax.scene.type.declaration.member.method.body.value.box.tag.FreeTagMarker;
import byteback.syntax.scene.type.declaration.member.method.tag.PostconditionsTag;
import byteback.syntax.scene.type.declaration.member.method.tag.PostconditionsTagAccessor;
import byteback.syntax.scene.type.declaration.member.method.tag.PreconditionsTag;
import byteback.syntax.scene.type.declaration.member.method.tag.PreconditionsTagAccessor;
import byteback.syntax.scene.type.declaration.tag.InvariantsTagAccessor;
import soot.SootClass;
import soot.SootMethod;
import soot.Value;
import soot.grimp.internal.ExprBox;

public class ClassInvariantExpander extends ClassTransformer {

	private static final Lazy<ClassInvariantExpander> INSTANCE = Lazy.from(ClassInvariantExpander::new);

	public static ClassInvariantExpander v() {
		return INSTANCE.get();
	}

	public void transformClass(final SootClass sootClass) {
		InvariantsTagAccessor.v().get(sootClass).ifPresent((invariantsTag) -> {
				for (final Value behaviorValue : invariantsTag.getConditions()) {
					for (final SootMethod sootMethod : sootClass.getMethods()) {
						if (!sootMethod.isStatic()) {
							final var preconditionBox = new ExprBox(behaviorValue);
							FreeTagMarker.v().flag(preconditionBox);
							final var postconditionBox = new ExprBox(behaviorValue);

							PreconditionsTagAccessor.v()
								.putIfAbsent(sootMethod, PreconditionsTag::new)
								.addConditionBox(preconditionBox);
							PostconditionsTagAccessor.v()
								.putIfAbsent(sootMethod, PostconditionsTag::new)
								.addConditionBox(postconditionBox);
						}
					}
				}
			});
	}

}
