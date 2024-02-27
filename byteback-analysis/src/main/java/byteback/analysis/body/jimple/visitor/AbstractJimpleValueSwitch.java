package byteback.analysis.body.jimple.visitor;

import byteback.analysis.common.visitor.Visitor;
import soot.Value;

public abstract class AbstractJimpleValueSwitch<T> extends soot.jimple.AbstractJimpleValueSwitch<T>
        implements Visitor<Value, T> {

    @Override
    public void defaultCase(final Object o) {
        defaultCase((Value) o);
    }

}