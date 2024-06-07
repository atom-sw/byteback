package byteback.syntax.scene.type.declaration.member.method.transformer;

import byteback.syntax.scene.type.declaration.transformer.ClassTransformer;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;

import java.util.ArrayList;
import java.util.List;

public abstract class MethodTransformer extends ClassTransformer {

    public abstract void transformMethod(final Scene scene, final SootClass sootClass, final SootMethod methodContext);

    @Override
    public void transformClass(final Scene scene, final SootClass sootClass) {
        if (sootClass.resolvingLevel() >= SootClass.SIGNATURES) {
            final List<SootMethod> methods = sootClass.getMethods();

            for (final SootMethod sootMethod : new ArrayList<>(methods)) {
                transformMethod(scene, sootClass, sootMethod);
            }
        }
    }

}
