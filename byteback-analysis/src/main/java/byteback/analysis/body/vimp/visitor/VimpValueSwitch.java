package byteback.analysis.body.vimp.visitor;

import byteback.analysis.body.vimp.syntax.*;
import byteback.analysis.common.visitor.Visitor;
import byteback.analysis.body.common.syntax.Value;

public interface VimpValueSwitch<T> extends Visitor<Value, T> {

    default void caseLogicAndExpr(final LogicAndExpr logicAndExpr) {
        defaultCase(logicAndExpr);
    }

    default void caseLogicOrExpr(final LogicOrExpr logicOrExpr) {
        defaultCase(logicOrExpr);
    }

    default void caseLogicXorExpr(final LogicXorExpr logicXorExpr) {
        defaultCase(logicXorExpr);
    }

    default void caseLogicImpliesExpr(final LogicImpliesExpr logicImpliesExpr) {
        defaultCase(logicImpliesExpr);
    }

    default void caseLogicIffExpr(final LogicIffExpr logicIffExpr) {
        defaultCase(logicIffExpr);
    }

    default void caseLogicForallExpr(final ForallExpr forallExpr) {
        defaultCase(forallExpr);
    }

    default void caseLogicExistsExpr(final ExistsExpr existsExpr) {
        defaultCase(existsExpr);
    }

    default void caseLogicNotExpr(final LogicNotExpr logicNotExpr) {
        defaultCase(logicNotExpr);
    }

    default void caseLogicConstant(final LogicConstant logicConstant) {
        defaultCase(logicConstant);
    }

    default void caseCallExpr(final CallExpr callExpr) {
        defaultCase(callExpr);
    }

    default void caseNestedExpr(final NestedExpr nestedExpr) {
        defaultCase(nestedExpr);
    }

}
