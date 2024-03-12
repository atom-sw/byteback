package byteback.analysis.scene.visitor;

import byteback.analysis.common.visitor.Visitor;
import soot.Type;

public abstract class AbstractTypeSwitch<R> extends soot.TypeSwitch<R> implements Visitor<Type, R> {
}
