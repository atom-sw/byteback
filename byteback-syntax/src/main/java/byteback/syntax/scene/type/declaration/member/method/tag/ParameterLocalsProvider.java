package byteback.syntax.scene.type.declaration.member.method.tag;

import byteback.common.function.Lazy;
import byteback.syntax.tag.TagProvider;
import soot.*;
import soot.javaToJimple.DefaultLocalGenerator;
import soot.jimple.JimpleBody;

import java.util.ArrayList;
import java.util.List;

public class ParameterLocalsProvider extends TagProvider<SootMethod, ParameterLocalsTag> {

    private static final Lazy<ParameterLocalsProvider> INSTANCE =
            Lazy.from(() -> new ParameterLocalsProvider(ParameterLocalsTag.NAME));

    public static ParameterLocalsProvider v() {
        return INSTANCE.get();
    }

    private ParameterLocalsProvider(final String tagName) {
        super(tagName);
    }

    @Override
    public ParameterLocalsTag compute(final SootMethod sootMethod) {
        final List<Local> locals;

        if (sootMethod.hasActiveBody()) {
            final Body body = sootMethod.getActiveBody();
            locals = body.getParameterLocals();
        } else {
            locals = new ArrayList<>();
            final var dummyBody = new JimpleBody();
            final var localGenerator = new DefaultLocalGenerator(dummyBody);

            for (final Type type : sootMethod.getParameterTypes()) {
                final Local local = localGenerator.generateLocal(type);
                locals.add(local);
            }
        }

        return new ParameterLocalsTag(locals);
    }

}