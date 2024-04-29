package byteback.syntax.scene.type.declaration.member.method.transformer;

import byteback.common.function.Lazy;
import byteback.syntax.scene.type.declaration.member.method.context.MethodContext;
import byteback.syntax.scene.type.declaration.member.method.tag.InputRefsTag;
import byteback.syntax.scene.type.declaration.member.method.tag.InputRefsTagAccessor;
import soot.*;
import soot.javaToJimple.DefaultLocalGenerator;
import soot.jimple.*;

import java.util.ArrayList;

public class IdentityStmtsTagger extends MethodTransformer {

    private static final Lazy<IdentityStmtsTagger> INSTANCE = Lazy.from(IdentityStmtsTagger::new);

    public static IdentityStmtsTagger v() {
        return INSTANCE.get();
    }

    private IdentityStmtsTagger() {
    }

    @Override
    public void transformMethod(final MethodContext methodContext) {
        final SootMethod sootMethod = methodContext.getSootMethod();
        final var identityStmts = new ArrayList<IdentityStmt>();

        if (sootMethod.hasActiveBody()) {
            // Find the locals in the body
            final Body body = sootMethod.getActiveBody();

            for (final Unit unit : body.getUnits()) {
                if (unit instanceof final IdentityStmt identityStmt) {
                    final Value rightOp = identityStmt.getRightOp();

                    if (rightOp instanceof ParameterRef || rightOp instanceof ThisRef) {
                        identityStmts.add(identityStmt);
                    }
                }
            }

        } else {
            // Create fake parameter locals
            final var dummyBody = new JimpleBody();
            final var localGenerator = new DefaultLocalGenerator(dummyBody);

            if (!sootMethod.isStatic()) {
                final RefType declaringType = sootMethod.getDeclaringClass().getType();
                final Local thisLocal = localGenerator.generateLocal(declaringType);
                final ThisRef thisRef = Jimple.v().newThisRef(declaringType);
                final IdentityStmt identityStmt = Jimple.v().newIdentityStmt(thisLocal, thisRef);
                identityStmts.add(identityStmt);
            }

            for (int i = 0; i < sootMethod.getParameterCount(); ++i) {
                final Type parameterType = sootMethod.getParameterType(i);
                final Local parameterLocal = localGenerator.generateLocal(parameterType);
                final ParameterRef parameterRef = Jimple.v().newParameterRef(parameterType, i);
                final IdentityStmt identityStmt = Jimple.v().newIdentityStmt(parameterLocal, parameterRef);
                identityStmts.add(identityStmt);
            }
        }

        final var parameterLocalsTag = new InputRefsTag(identityStmts);
        InputRefsTagAccessor.v().put(sootMethod, parameterLocalsTag);
    }

}
