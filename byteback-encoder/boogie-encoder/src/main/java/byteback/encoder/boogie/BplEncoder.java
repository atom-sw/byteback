package byteback.encoder.boogie;

import byteback.analysis.Scheduler;
import byteback.encoder.Separator;
import java.util.Stack;
import sootup.core.signatures.FieldSignature;
import sootup.core.signatures.MethodSignature;
import sootup.core.types.ClassType;

public class BplEncoder implements ClassTypeToBplEncoder, MethodSignatureToBplEncoder, FieldSignatureToBplEncoder {

	private final BplContext context;

	private final Stack<Separator> separatorStack;

	public BplEncoder(final BplContext context) {
		this.context = context;
		this.separatorStack = new Stack<>();
	}

	public BplContext getContext() {
		return context;
	}

	public Stack<Separator> getSeparatorStack() {
		return separatorStack;
	}

	public void encodeAll() {
		final Scheduler scheduler = getContext().getScheduler();
		while (true) {
			if (scheduler.hasNextClassType()) {
				final ClassType classType = scheduler.pollClassType();
				encodeHierarchy(classType);
				continue;
			}
			if (scheduler.hasNextFieldSignature()) {
				final FieldSignature fieldSignature = scheduler.pollFieldSignature();
				encodeField(fieldSignature);
				continue;
			}
			if (scheduler.hasNextMethodSignature()) {
				final MethodSignature methodSignature = scheduler.pollMethodSignature();
				encodeMethodSignature(methodSignature);
				continue;
			}
			return;
		}
	}

}
