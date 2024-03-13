package byteback.analysis.model.syntax.type;

import byteback.analysis.model.syntax.signature.ClassSignature;
import byteback.analysis.model.syntax.type.visitor.TypeSwitch;

public class ClassType extends Type implements ClassSignature, Comparable<ClassType> {

    private final String name;

    public ClassType(final String name) {
        if (!name.isEmpty()) {
            if (name.charAt(0) == '[') {
                throw new RuntimeException("Attempt to create RefType whose name starts with [ --> " + name);
            }

            if (name.indexOf('/') >= 0) {
                throw new RuntimeException("Attempt to create RefType containing a / --> " + name);
            }

            if (name.indexOf(';') >= 0) {
                throw new RuntimeException("Attempt to create RefType containing a ; --> " + name);
            }
        }

        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int compareTo(ClassType t) {
        return this.toString().compareTo(t.toString());
    }

    @Override
    public void apply(final TypeSwitch<?> typeSwitch) {
        typeSwitch.caseClassType(this);
    }
}
