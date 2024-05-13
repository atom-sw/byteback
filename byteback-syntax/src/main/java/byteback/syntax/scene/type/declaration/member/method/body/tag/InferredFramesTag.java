package byteback.syntax.scene.type.declaration.member.method.body.tag;

import soot.jimple.ConcreteRef;
import soot.tagkit.AttributeValueException;
import soot.tagkit.Tag;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class InferredFramesTag implements Tag {

	public static final String NAME = "InferredFramesTag";

	final Set<ConcreteRef> frameRefs;

	/**
	 * Constructs a new {@link InferredFramesTag}.
	 *
	 * @param frameRefs The references in the frame condition.
	 */
	public InferredFramesTag(final Set<ConcreteRef> frameRefs) {
		this.frameRefs = frameRefs;
	}

	/**
	 * Constructs a new empty {@link InferredFramesTag}.
	 */
	public InferredFramesTag() {
		this(new HashSet<>());
	}

	public Set<ConcreteRef> getFrameRefs() {
		return Collections.unmodifiableSet(frameRefs);
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public byte[] getValue() throws AttributeValueException {
		return new byte[0];
	}

}
