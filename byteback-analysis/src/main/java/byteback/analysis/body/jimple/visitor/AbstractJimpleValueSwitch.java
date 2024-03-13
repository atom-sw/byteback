package byteback.analysis.body.jimple.visitor;

import byteback.analysis.common.visitor.Visitor;
import byteback.analysis.body.common.syntax.Value;

public abstract class AbstractJimpleValueSwitch<T> extends byteback.analysis.body.jimple.syntax.AbstractJimpleValueSwitch<T>
        implements Visitor<Value, T> {

    @Override
    public void defaultCase(final Object object) {
        defaultCase((Value) object);
    }

}