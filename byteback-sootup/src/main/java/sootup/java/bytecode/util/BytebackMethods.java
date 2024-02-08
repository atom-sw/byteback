package sootup.java.bytecode.util;

import sootup.java.core.AnnotationUsage;
import sootup.java.core.JavaSootMethod;

import java.util.Optional;

public class BytebackMethods {

    public boolean isPure(final JavaSootMethod method) {
        for (final AnnotationUsage annotationUsage : method.getAnnotations(Optional.empty())) {
            final String annotationName = annotationUsage.getAnnotation().getClassName();
            if (annotationName.equals(BytebackNamespace.PURE_ANNOTATION_NAME)) {
                return true;
            }
        }
        return false;
    }

}
