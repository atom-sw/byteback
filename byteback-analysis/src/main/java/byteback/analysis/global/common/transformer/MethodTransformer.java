package byteback.analysis.global.common.transformer;

import soot.Scene;
import soot.SootClass;
import soot.SootMethod;

public abstract class MethodTransformer extends ClassTransformer {

    @Override
    public void transformClass(final Scene scene, final SootClass sootClass) {
        if (sootClass.resolvingLevel() >= SootClass.SIGNATURES) {
            for (final SootMethod sootMethod : sootClass.getMethods()) {
                transformMethod(scene, sootMethod);
            }
        }
    }

    public abstract void transformMethod(final Scene scene, final SootMethod sootMethod) ;

}
