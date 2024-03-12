package soot.jimple.toolkits.scalar;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1999 Phong Co
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */

import soot.Value;
import soot.jimple.*;

public class Evaluator {

    public static boolean isValueConstantValued(Value op) {
        if (op instanceof Constant) {
            return true;
        } else if (op instanceof UnopExpr) {
            Value innerOp = ((UnopExpr) op).getOp();
            if (innerOp == NullConstant.v()) {
                // operations on null will throw an exception and the operation
                // is therefore not considered constant-valued; see posting on Soot list
                // on 18 September 2007 14:36
                return false;
            }
            return isValueConstantValued(innerOp);
        } else if (op instanceof BinopExpr binExpr) {
            final Value op1 = binExpr.getOp1();
            final Value op2 = binExpr.getOp2();

            // Only evaluate these checks once, and use the result multiple times
            final boolean isOp1Constant = isValueConstantValued(op1);
            final boolean isOp2Constant = isValueConstantValued(op2);

            /* Handle weird cases. */
            if (op instanceof DivExpr || op instanceof RemExpr) {
                if (!isOp1Constant || !isOp2Constant) {
                    return false;
                }

                /* check for a 0 value. If so, punt. */
                Value c2 = getConstantValueOf(op2);
                if ((c2 instanceof IntConstant && ((IntConstant) c2).value == 0)
                        || (c2 instanceof LongConstant && ((LongConstant) c2).value == 0)) {
                    return false;
                }
            }

            return isOp1Constant && isOp2Constant;
        }
        return false;
    } // isValueConstantValued

    /**
     * Returns the constant value of <code>op</code> if it is easy to find the constant value; else returns <code>null</code>.
     */
    public static Value getConstantValueOf(Value op) {
        if (!isValueConstantValued(op)) {
            return null;
        }

        if (op instanceof Constant) {
            return op;
        } else if (op instanceof UnopExpr) {
            Value c = getConstantValueOf(((UnopExpr) op).getOp());
            if (op instanceof NegExpr) {
                return ((NumericConstant) c).negate();
            }
        } else if (op instanceof BinopExpr binExpr) {
            final Value c1 = getConstantValueOf(binExpr.getOp1());
            final Value c2 = getConstantValueOf(binExpr.getOp2());

            if (op instanceof AddExpr) {
                return ((NumericConstant) c1).add((NumericConstant) c2);
            } else if (op instanceof SubExpr) {
                return ((NumericConstant) c1).subtract((NumericConstant) c2);
            } else if (op instanceof MulExpr) {
                return ((NumericConstant) c1).multiply((NumericConstant) c2);
            } else if (op instanceof DivExpr) {
                return ((NumericConstant) c1).divide((NumericConstant) c2);
            } else if (op instanceof RemExpr) {
                return ((NumericConstant) c1).remainder((NumericConstant) c2);
            } else if (op instanceof EqExpr || op instanceof NeExpr) {
                if (c1 instanceof NumericConstant) {
                    if (!(c2 instanceof NumericConstant)) {
                        return IntConstant.v(0);
                    } else if (op instanceof EqExpr) {
                        return ((NumericConstant) c1).equalEqual((NumericConstant) c2);
                    } else if (op instanceof NeExpr) {
                        return ((NumericConstant) c1).notEqual((NumericConstant) c2);
                    }
                } else if (c1 instanceof StringConstant || c1 instanceof NullConstant || c1 instanceof ClassConstant) {
                    boolean equality = c1.equals(c2);
                    boolean truth = (op instanceof EqExpr) == equality;
                    return IntConstant.v(truth ? 1 : 0);
                }
                throw new RuntimeException("constant neither numeric nor string");
            } else if (op instanceof GtExpr) {
                return ((NumericConstant) c1).greaterThan((NumericConstant) c2);
            } else if (op instanceof GeExpr) {
                return ((NumericConstant) c1).greaterThanOrEqual((NumericConstant) c2);
            } else if (op instanceof LtExpr) {
                return ((NumericConstant) c1).lessThan((NumericConstant) c2);
            } else if (op instanceof LeExpr) {
                return ((NumericConstant) c1).lessThanOrEqual((NumericConstant) c2);
            } else if (op instanceof AndExpr) {
                return ((ArithmeticConstant) c1).and((ArithmeticConstant) c2);
            } else if (op instanceof OrExpr) {
                return ((ArithmeticConstant) c1).or((ArithmeticConstant) c2);
            } else if (op instanceof XorExpr) {
                return ((ArithmeticConstant) c1).xor((ArithmeticConstant) c2);
            } else if (op instanceof ShlExpr) {
                return ((ArithmeticConstant) c1).shiftLeft((ArithmeticConstant) c2);
            } else if (op instanceof ShrExpr) {
                return ((ArithmeticConstant) c1).shiftRight((ArithmeticConstant) c2);
            } else if (op instanceof UshrExpr) {
                return ((ArithmeticConstant) c1).unsignedShiftRight((ArithmeticConstant) c2);
            } else if (op instanceof CmpExpr) {
                if ((c1 instanceof LongConstant) && (c2 instanceof LongConstant)) {
                    return ((LongConstant) c1).cmp((LongConstant) c2);
                } else {
                    throw new IllegalArgumentException("CmpExpr: LongConstant(s) expected");
                }
            } else if ((op instanceof CmpgExpr) || (op instanceof CmplExpr)) {
                if ((c1 instanceof RealConstant) && (c2 instanceof RealConstant)) {
                    if (op instanceof CmpgExpr) {
                        return ((RealConstant) c1).cmpg((RealConstant) c2);
                    } else if (op instanceof CmplExpr) {
                        return ((RealConstant) c1).cmpl((RealConstant) c2);
                    }
                } else {
                    throw new IllegalArgumentException("CmpExpr: RealConstant(s) expected");
                }
            } else {
                throw new RuntimeException("unknown binop: " + op);
            }
        }

        throw new RuntimeException("couldn't getConstantValueOf of: " + op);
    } // getConstantValueOf

} // Evaluator
