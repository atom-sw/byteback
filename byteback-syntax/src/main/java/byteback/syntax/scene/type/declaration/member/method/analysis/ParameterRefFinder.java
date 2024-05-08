package byteback.syntax.scene.type.declaration.member.method.analysis;

import byteback.common.function.Lazy;
import byteback.syntax.scene.type.declaration.member.method.body.Vimp;
import byteback.syntax.scene.type.declaration.member.method.body.value.ReturnRef;
import byteback.syntax.scene.type.declaration.member.method.body.value.ThrownRef;
import byteback.syntax.scene.type.declaration.member.method.tag.BehaviorTagMarker;
import soot.*;
import soot.jimple.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ParameterRefFinder {

    private static final Lazy<ParameterRefFinder> INSTANCE = Lazy.from(ParameterRefFinder::new);

    public static ParameterRefFinder v() {
        return INSTANCE.get();
    }

    private ParameterRefFinder() {
    }

    public List<IdentityRef> findInputRefs(final SootMethod sootMethod) {
        final var inputLocals = new ArrayList<IdentityRef>();

        if (!sootMethod.isStatic()) {
            final SootClass declaringClass = sootMethod.getDeclaringClass();
            final RefType thisType = declaringClass.getType();
            final ThisRef thisRef = Jimple.v().newThisRef(thisType);
            inputLocals.add(thisRef);
        }

        for (int parameterIndex = 0; parameterIndex < sootMethod.getParameterCount(); ++parameterIndex) {
            final Type argumentType = sootMethod.getParameterType(parameterIndex);
            final ParameterRef argumentLocal = Jimple.v().newParameterRef(argumentType, parameterIndex);
            inputLocals.add(argumentLocal);
        }

        return Collections.unmodifiableList(inputLocals);
    }

    public List<ConcreteRef> findOutputRefs(final SootMethod sootMethod) {
        final var outputRefs = new ArrayList<ConcreteRef>();
        final Type returnType = sootMethod.getReturnType();

        if (returnType != VoidType.v()) {
            final ReturnRef returnRef = Vimp.v().newReturnRef(returnType);
            outputRefs.add(returnRef);
        }

        if (!BehaviorTagMarker.v().hasTag(sootMethod)) {
            final ThrownRef thrownRef = Vimp.v().newThrownRef();
            outputRefs.add(thrownRef);
        }

        return Collections.unmodifiableList(outputRefs);
    }

}
