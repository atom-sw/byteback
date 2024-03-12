package soot.baf.syntax;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1999 Patrick Lam, Patrick Pominville and Raja Vallee-Rai
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

import soot.util.Switch;

public interface InstSwitch extends Switch {
    void caseReturnVoidInst(ReturnVoidInst i);

    void caseReturnInst(ReturnInst i);

    void caseNopInst(NopInst i);

    void caseGotoInst(GotoInst i);

    void caseJSRInst(JSRInst i);

    void casePushInst(PushInst i);

    void casePopInst(PopInst i);

    void caseIdentityInst(IdentityInst i);

    void caseStoreInst(StoreInst i);

    void caseLoadInst(LoadInst i);

    void caseArrayWriteInst(ArrayWriteInst i);

    void caseArrayReadInst(ArrayReadInst i);

    void caseIfNullInst(IfNullInst i);

    void caseIfNonNullInst(IfNonNullInst i);

    void caseIfEqInst(IfEqInst i);

    void caseIfNeInst(IfNeInst i);

    void caseIfGtInst(IfGtInst i);

    void caseIfGeInst(IfGeInst i);

    void caseIfLtInst(IfLtInst i);

    void caseIfLeInst(IfLeInst i);

    void caseIfCmpEqInst(IfCmpEqInst i);

    void caseIfCmpNeInst(IfCmpNeInst i);

    void caseIfCmpGtInst(IfCmpGtInst i);

    void caseIfCmpGeInst(IfCmpGeInst i);

    void caseIfCmpLtInst(IfCmpLtInst i);

    void caseIfCmpLeInst(IfCmpLeInst i);

    void caseStaticGetInst(StaticGetInst i);

    void caseStaticPutInst(StaticPutInst i);

    void caseFieldGetInst(FieldGetInst i);

    void caseFieldPutInst(FieldPutInst i);

    void caseInstanceCastInst(InstanceCastInst i);

    void caseInstanceOfInst(InstanceOfInst i);

    void casePrimitiveCastInst(PrimitiveCastInst i);

    void caseDynamicInvokeInst(DynamicInvokeInst i);

    void caseStaticInvokeInst(StaticInvokeInst i);

    void caseVirtualInvokeInst(VirtualInvokeInst i);

    void caseInterfaceInvokeInst(InterfaceInvokeInst i);

    void caseSpecialInvokeInst(SpecialInvokeInst i);

    void caseThrowInst(ThrowInst i);

    void caseAddInst(AddInst i);

    void caseAndInst(AndInst i);

    void caseOrInst(OrInst i);

    void caseXorInst(XorInst i);

    void caseArrayLengthInst(ArrayLengthInst i);

    void caseCmpInst(CmpInst i);

    void caseCmpgInst(CmpgInst i);

    void caseCmplInst(CmplInst i);

    void caseDivInst(DivInst i);

    void caseIncInst(IncInst i);

    void caseMulInst(MulInst i);

    void caseRemInst(RemInst i);

    void caseSubInst(SubInst i);

    void caseShlInst(ShlInst i);

    void caseShrInst(ShrInst i);

    void caseUshrInst(UshrInst i);

    void caseNewInst(NewInst i);

    void caseNegInst(NegInst i);

    void caseSwapInst(SwapInst i);

    void caseDup1Inst(Dup1Inst i);

    void caseDup2Inst(Dup2Inst i);

    void caseDup1_x1Inst(Dup1_x1Inst i);

    void caseDup1_x2Inst(Dup1_x2Inst i);

    void caseDup2_x1Inst(Dup2_x1Inst i);

    void caseDup2_x2Inst(Dup2_x2Inst i);

    void caseNewArrayInst(NewArrayInst i);

    void caseNewMultiArrayInst(NewMultiArrayInst i);

    void caseLookupSwitchInst(LookupSwitchInst i);

    void caseTableSwitchInst(TableSwitchInst i);

    void caseEnterMonitorInst(EnterMonitorInst i);

    void caseExitMonitorInst(ExitMonitorInst i);
}
