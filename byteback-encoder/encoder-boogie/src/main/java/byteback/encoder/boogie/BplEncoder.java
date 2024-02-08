package byteback.encoder.boogie;

import byteback.encoder.common.Scheduler;
import byteback.encoder.common.Separator;
import java.util.Queue;
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
		final Queue<ClassType> nextClassTypes = scheduler.getNextClassTypes();
		final Queue<FieldSignature> nextFieldSignatures = scheduler.getNextFieldSignatures();
		final Queue<MethodSignature> nextMethodSignatures = scheduler.getNextMethodSignatures();
		while (true) {
			if (!nextClassTypes.isEmpty()) {
				final ClassType classType = nextClassTypes.poll();
				encodeHierarchy(classType);
				continue;
			}
			if (!nextFieldSignatures.isEmpty()) {
				final FieldSignature fieldSignature = nextFieldSignatures.poll();
				encodeField(fieldSignature);
				continue;
			}
			if (!nextMethodSignatures.isEmpty()) {
				final MethodSignature methodSignature = nextMethodSignatures.poll();
				encodeMethodSignature(methodSignature);
				continue;
			}
			return;
		}
	}

}
