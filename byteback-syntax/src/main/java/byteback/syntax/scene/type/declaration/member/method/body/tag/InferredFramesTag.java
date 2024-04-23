package byteback.syntax.scene.type.declaration.member.method.body.tag;

import byteback.syntax.tag.ValuesTag;
import soot.jimple.ConcreteRef;

import java.util.ArrayList;
import java.util.List;

public class InferredFramesTag extends ValuesTag<ConcreteRef> {

    public static final String NAME = "InferredFramesTag";

    /**
     * Constructs a new {@link InferredFramesTag}.
     *
     * @param concreteRefs The references in the frame condition.
     */
    public InferredFramesTag(final List<ConcreteRef> concreteRefs) {
        super(concreteRefs);
    }

    /**
     * Constructs a new empty {@link InferredFramesTag}.
     */
    public InferredFramesTag() {
        this(new ArrayList<>());
    }

    @Override
    public String getName() {
        return NAME;
    }

}
