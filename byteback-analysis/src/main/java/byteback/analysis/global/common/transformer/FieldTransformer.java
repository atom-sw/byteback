package byteback.analysis.global.common.transformer;

import soot.Scene;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;

public abstract class FieldTransformer extends ClassTransformer {

    @Override
    public void transformClass(final Scene scene, final SootClass sootClass) {
        if (sootClass.resolvingLevel() >= SootClass.SIGNATURES) {
            for (final SootField sootField : sootClass.getFields()) {
                transformField(scene, sootField);
            }
        }
    }

    public abstract void transformField(final Scene scene, final SootField sootField) ;

}
