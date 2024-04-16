package byteback.syntax.scene.type.declaration.member.method.body.tag;

import byteback.syntax.tag.ValuesTag;
import soot.jimple.ConcreteRef;

import java.util.ArrayList;
import java.util.List;

public class InferredLocalFramesTag extends ValuesTag<ConcreteRef> {

    public static final String NAME = "InferredFramesTag";

    /**
     * Constructs a new {@link InferredLocalFramesTag}.
     *
     * @param concreteRefs The references in the frame condition.
     */
    public InferredLocalFramesTag(final List<ConcreteRef> concreteRefs) {
        super(concreteRefs);
    }

    /**
     * Constructs a new empty {@link InferredLocalFramesTag}.
     */
    public InferredLocalFramesTag() {
        this(new ArrayList<>());
    }

    @Override
    public String getName() {
        return NAME;
    }

}
