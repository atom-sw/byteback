package byteback.syntax.scene.type.declaration.member.method.transformer;

import byteback.common.function.Lazy;
import byteback.syntax.scene.type.declaration.member.method.context.MethodContext;
import byteback.syntax.scene.type.declaration.member.method.tag.ParameterLocalsTag;
import byteback.syntax.scene.type.declaration.member.method.tag.ParameterLocalsTagProvider;
import soot.*;
import soot.javaToJimple.DefaultLocalGenerator;
import soot.jimple.JimpleBody;

import java.util.ArrayList;

public class ParameterLocalsTagger extends MethodTransformer {

    private static final Lazy<ParameterLocalsTagger> INSTANCE = Lazy.from(ParameterLocalsTagger::new);

    public static ParameterLocalsTagger v() {
        return INSTANCE.get();
    }

    private ParameterLocalsTagger() {
    }

    @Override
    public void transformMethod(final MethodContext methodContext) {
        final SootMethod sootMethod = methodContext.getSootMethod();
        final var parameterLocals = new ArrayList<Local>();

        if (sootMethod.hasActiveBody()) {
            // Find the locals in the body
            final Body body = sootMethod.getActiveBody();

            if (!sootMethod.isStatic()) {
                parameterLocals.add(body.getThisLocal());
            }

            parameterLocals.addAll(body.getParameterLocals());
        } else {
            // Create fake parameter locals
            final var dummyBody = new JimpleBody();
            final var localGenerator = new DefaultLocalGenerator(dummyBody);

            if (!sootMethod.isStatic()) {
                parameterLocals.add(localGenerator.generateLocal(sootMethod.getDeclaringClass().getType()));
            }

            for (final Type type : sootMethod.getParameterTypes()) {
                final Local local = localGenerator.generateLocal(type);
                parameterLocals.add(local);
            }
        }

        final var parameterLocalsTag = new ParameterLocalsTag(parameterLocals);
        ParameterLocalsTagProvider.v().put(sootMethod, parameterLocalsTag);
    }

}
