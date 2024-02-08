package byteback.analysis;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import sootup.core.signatures.FieldSignature;
import sootup.core.signatures.MethodSignature;
import sootup.core.signatures.Signature;
import sootup.core.types.ClassType;
import sootup.core.views.View;

public class Scheduler {

	private final Set<Signature> scheduledSignatures;

	private final Queue<ClassType> nextClassTypes;

	private final Queue<FieldSignature> nextFieldSignatures;

	private final Queue<MethodSignature> nextMethodSignatures;

	public Scheduler(final Set<Signature> scheduledSignatures, final Queue<ClassType> nextClassTypes,
			final Queue<FieldSignature> nextFieldSignatures, final Queue<MethodSignature> nextMethodSignatures) {
		this.scheduledSignatures = scheduledSignatures;
		this.nextClassTypes = nextClassTypes;
		this.nextFieldSignatures = nextFieldSignatures;
		this.nextMethodSignatures = nextMethodSignatures;
	}

	public Scheduler(final View<?> view) {
		this(new HashSet<>(), new LinkedList<>(), new LinkedList<>(), new LinkedList<>());
	}

	public Queue<ClassType> getNextClassTypes() {
		return nextClassTypes;
	}

	public Queue<FieldSignature> getNextFieldSignatures() {
		return nextFieldSignatures;
	}

	public Queue<MethodSignature> getNextMethodSignatures() {
		return nextMethodSignatures;
	}

	private <T extends Signature> void scheduleSignature(final T signature, final Queue<T> nextSignatures) {
		if (!isScheduled(signature)) {
			nextSignatures.add(signature);
			scheduledSignatures.add(signature);
		}
	}

	public boolean isScheduled(final Signature signature) {
		return scheduledSignatures.contains(signature);
	}

	public void schedule(final ClassType classType) {
		scheduleSignature(classType, nextClassTypes);
	}

	public void schedule(final FieldSignature fieldSignature) {
		scheduleSignature(fieldSignature, nextFieldSignatures);
	}

	public void schedule(final MethodSignature methodSignature) {
		scheduleSignature(methodSignature, nextMethodSignatures);
	}

}
