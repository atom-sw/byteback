package byteback.analysis.body.common.source;

import byteback.analysis.model.syntax.ClassModel;

public abstract class ClassSource {

    protected final String className;

    public ClassSource(final String className) {
        if (className == null) {
            throw new IllegalArgumentException("The class name must not be null");
        }

        this.className = className;
    }

    public abstract Dependencies resolve(ClassModel classModel);
}
