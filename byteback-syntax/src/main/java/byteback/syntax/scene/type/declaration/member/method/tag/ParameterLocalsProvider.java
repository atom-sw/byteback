package byteback.syntax.scene.type.declaration.member.method.tag;

import byteback.common.function.Lazy;
import byteback.syntax.tag.TagProvider;
import soot.*;
import soot.javaToJimple.DefaultLocalGenerator;
import soot.jimple.JimpleBody;

import java.util.*;

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
        final var locals = new ArrayList<Local>();

        if (sootMethod.hasActiveBody()) {
            final Body body = sootMethod.getActiveBody();

            if (!sootMethod.isStatic()) {
                locals.add(body.getThisLocal());
            }

            locals.addAll(body.getParameterLocals());
        } else {
            final var dummyBody = new JimpleBody();
            final var localGenerator = new DefaultLocalGenerator(dummyBody);

            if (!sootMethod.isStatic()) {
                locals.add(localGenerator.generateLocal(sootMethod.getDeclaringClass().getType()));
            }

            for (final Type type : sootMethod.getParameterTypes()) {
                final Local local = localGenerator.generateLocal(type);
                locals.add(local);
            }
        }

        return new ParameterLocalsTag(locals);
    }

}