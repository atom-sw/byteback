package byteback.analysis.scene.visitor;

import byteback.analysis.common.visitor.Visitor;
import soot.DoubleType;
import soot.FloatType;
import soot.PrimType;
import soot.Type;

public abstract class AbstractTypeSwitch<R> extends soot.TypeSwitch<R> implements Visitor<Type, R> {
}
