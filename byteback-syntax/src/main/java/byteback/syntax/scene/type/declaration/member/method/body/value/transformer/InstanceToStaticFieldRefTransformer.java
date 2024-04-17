package byteback.syntax.scene.type.declaration.member.method.body.value.transformer;

import byteback.syntax.Vimp;
import byteback.syntax.scene.type.declaration.member.method.body.value.TypeConstant;
import byteback.syntax.scene.type.declaration.member.method.body.value.context.ValueContext;
import soot.*;
import soot.grimp.Grimp;
import soot.jimple.Jimple;
import soot.jimple.StaticFieldRef;

public class InstanceToStaticFieldRefTransformer extends ValueTransformer {

    @Override
    public void transformValue(final ValueContext valueContext) {
        final ValueBox valueBox = valueContext.getValueBox();
        final Value value = valueBox.getValue();

        if (value instanceof final StaticFieldRef staticFieldRef) {
            final SootFieldRef fieldRef = staticFieldRef.getFieldRef();
            final SootClass declaringClass = fieldRef.declaringClass();
            final RefType declaringType = declaringClass.getType();
            final TypeConstant typeConstant = Vimp.v().newTypeConstant(declaringType);
            valueBox.setValue(Grimp.v().newInstanceFieldRef(typeConstant, fieldRef));
        }
    }

}
