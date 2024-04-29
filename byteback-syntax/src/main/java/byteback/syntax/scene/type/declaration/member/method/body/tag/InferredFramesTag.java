package byteback.syntax.scene.type.declaration.member.method.body.tag;

import soot.jimple.ConcreteRef;
import soot.tagkit.AttributeValueException;
import soot.tagkit.Tag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InferredFramesTag implements Tag {

    public static final String NAME = "InferredFramesTag";

    final List<ConcreteRef> frameRefs;

    /**
     * Constructs a new {@link InferredFramesTag}.
     *
     * @param frameRefs The references in the frame condition.
     */
    public InferredFramesTag(final List<ConcreteRef> frameRefs) {
        this.frameRefs = frameRefs;
    }

    /**
     * Constructs a new empty {@link InferredFramesTag}.
     */
    public InferredFramesTag() {
        this(new ArrayList<>());
    }

    public List<ConcreteRef> getFrameRefs() {
        return Collections.unmodifiableList(frameRefs);
    }

    public void addFrameRef(final ConcreteRef concreteRef) {
        frameRefs.add(concreteRef);
    }

    public void removeFrameRef(final ConcreteRef concreteRef) {
        frameRefs.remove(concreteRef);
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
