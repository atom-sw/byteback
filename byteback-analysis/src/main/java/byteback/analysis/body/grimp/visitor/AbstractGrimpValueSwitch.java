package byteback.analysis.body.jimple.visitor;

import byteback.analysis.common.visitor.Visitor;
import soot.Value;

public abstract class AbstractGrimpValueSwitch<T> extends soot.grimp.AbstractGrimpValueSwitch<T>
        implements Visitor<Value, T> {
}
