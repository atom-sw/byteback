package byteback.analysis.body.jimple.syntax;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 1999 Raja Vallee-Rai
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

public interface ExprSwitch extends soot.util.Switch {
    void caseAddExpr(AddExpr v);

    void caseAndExpr(AndExpr v);

    void caseCmpExpr(CmpExpr v);

    void caseCmpgExpr(CmpgExpr v);

    void caseCmplExpr(CmplExpr v);

    void caseDivExpr(DivExpr v);

    void caseEqExpr(EqExpr v);

    void caseNeExpr(NeExpr v);

    void caseGeExpr(GeExpr v);

    void caseGtExpr(GtExpr v);

    void caseLeExpr(LeExpr v);

    void caseLtExpr(LtExpr v);

    void caseMulExpr(MulExpr v);

    void caseOrExpr(OrExpr v);

    void caseRemExpr(RemExpr v);

    void caseShlExpr(ShlExpr v);

    void caseShrExpr(ShrExpr v);

    void caseUshrExpr(UshrExpr v);

    void caseSubExpr(SubExpr v);

    void caseXorExpr(XorExpr v);

    void caseInterfaceInvokeExpr(InterfaceInvokeExpr v);

    void caseSpecialInvokeExpr(SpecialInvokeExpr v);

    void caseStaticInvokeExpr(StaticInvokeExpr v);

    void caseVirtualInvokeExpr(VirtualInvokeExpr v);

    void caseDynamicInvokeExpr(DynamicInvokeExpr v);

    void caseCastExpr(CastExpr v);

    void caseInstanceOfExpr(InstanceOfExpr v);

    void caseNewArrayExpr(NewArrayExpr v);

    void caseNewMultiArrayExpr(NewMultiArrayExpr v);

    void caseNewExpr(NewExpr v);

    void caseLengthExpr(LengthExpr v);

    void caseNegExpr(NegExpr v);

    void defaultCase(Object obj);
}
