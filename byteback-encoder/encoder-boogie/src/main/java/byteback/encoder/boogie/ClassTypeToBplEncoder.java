package byteback.encoder.boogie;

import byteback.encoder.common.Scheduler;
import java.util.Optional;
import sootup.core.frontend.ResolveException;
import sootup.core.model.SootClass;
import sootup.core.model.SootMethod;
import sootup.core.types.ClassType;
import sootup.core.views.View;

public interface ClassTypeToBplEncoder
		extends
			TypeToBplEncoder,
			BplConstEncoder,
			BplAxiomEncoder,
			BplBinaryOperationEncoder {

	default void encodeClassTypeDeclaration(final ClassType classType) {
		encodeConst();
		encodeClassTypeIdentifier(classType);
		encode(BplSyntax.TYPE_SEPARATOR);
		encodeSpace();
		encode(BplPrelude.TYPE_TYPE);
		encode(BplSyntax.LINE_TERMINATOR);
		encodeNewline();
	}

	default void encodeInheritanceAxiom(final ClassType classType, final ClassType superType) {
		encodeAxiom();
		encodeClassTypeIdentifier(classType);
		encodeBinaryOperation(BplSyntax.PARTIAL_OP);
		encodeClassTypeIdentifier(superType);
		encode(BplSyntax.LINE_TERMINATOR);
		encodeNewline();
	}

	default void encodeHierarchy(final ClassType classType) {
		encodeClassTypeDeclaration(classType);
		final View<?> view = getView();
		final Scheduler scheduler = getScheduler();
		try {
			final SootClass<?> sootClass = view.getClassOrThrow(classType);
			final Optional<? extends ClassType> superTypeOptional = sootClass.getSuperclass();
			if (superTypeOptional.isPresent()) {
				final ClassType superType = superTypeOptional.get();
				scheduler.schedule(superType);
				encodeInheritanceAxiom(classType, superType);
			}
			for (final SootMethod sootMethod : sootClass.getMethods()) {
				scheduler.schedule(sootMethod.getSignature());
			}
		} catch (final ResolveException exception) {
		}

	}

}
