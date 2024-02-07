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

	private <T extends Signature> void scheduleSignature(final T signature, final Queue<T> nextSignatures) {
		if (!isScheduled(signature)) {
			nextSignatures.add(signature);
			scheduledSignatures.add(signature);
		}
	}

	private <T extends Signature> void scheduleSignatures(final Iterable<T> signatures, final Queue<T> nextSignatures) {
		for (final T signature : signatures) {
			scheduleSignature(signature, nextSignatures);
		}
	}

	public boolean isScheduled(final Signature signature) {
		return scheduledSignatures.contains(signature);
	}

	public void scheduleClassType(final ClassType classType) {
		scheduleSignature(classType, nextClassTypes);
	}

	public void scheduleClassTypes(final Iterable<ClassType> classTypes) {
		scheduleSignatures(classTypes, nextClassTypes);
	}

	public ClassType pollClassType() {
		return nextClassTypes.poll();
	}

	public boolean hasNextClassType() {
		return !nextClassTypes.isEmpty();
	}

	public void scheduleFieldSignature(final FieldSignature fieldSignature) {
		scheduleSignature(fieldSignature, nextFieldSignatures);
	}

	public void scheduleFieldSignatures(final Iterable<FieldSignature> fieldSignatures) {
		scheduleSignatures(fieldSignatures, nextFieldSignatures);
	}

	public FieldSignature pollFieldSignature() {
		return nextFieldSignatures.poll();
	}

	public boolean hasNextFieldSignature() {
		return !nextFieldSignatures.isEmpty();
	}

	public void scheduleMethodSignature(final MethodSignature methodSignature) {
		scheduleSignature(methodSignature, nextMethodSignatures);
	}

	public void scheduleMethodSignatures(final Iterable<MethodSignature> fieldSignatures) {
		scheduleSignatures(fieldSignatures, nextMethodSignatures);
	}

	public MethodSignature pollMethodSignature() {
		return nextMethodSignatures.poll();
	}

	public boolean hasNextMethodSignature() {
		return !nextMethodSignatures.isEmpty();
	}

}
