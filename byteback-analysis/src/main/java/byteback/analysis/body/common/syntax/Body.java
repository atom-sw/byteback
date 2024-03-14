package byteback.analysis.body.common.syntax;

import byteback.analysis.body.common.syntax.expr.ParameterRef;
import byteback.analysis.body.common.syntax.expr.Value;
import byteback.analysis.body.common.syntax.expr.ValueBox;
import byteback.analysis.body.common.syntax.stmt.Trap;
import byteback.analysis.body.common.syntax.stmt.Unit;
import byteback.analysis.body.common.syntax.stmt.UnitBox;
import byteback.analysis.body.common.syntax.stmt.UnitPatchingChain;
import byteback.analysis.body.jimple.syntax.expr.Local;
import byteback.analysis.body.common.syntax.expr.ThisRef;
import byteback.analysis.body.jimple.syntax.stmt.IdentityStmt;
import byteback.analysis.common.syntax.Chain;
import byteback.analysis.common.syntax.HashChain;
import byteback.analysis.model.syntax.MethodModel;

import java.io.Serializable;
import java.util.*;

public abstract class Body implements Serializable {

    /**
     * The method associated with this Body.
     */
    protected final MethodModel methodModel;

    /**
     * The chain of locals for this Body.
     */
    protected Chain<Local> localChain = new HashChain<>();

    /**
     * The chain of traps for this Body.
     */
    protected Chain<Trap> traps = new HashChain<>();

    /**
     * The chain of units for this Body.
     */
    protected UnitPatchingChain units = new UnitPatchingChain(new HashChain<>());

    abstract public Object clone(boolean noLocalsClone);

    protected Body(final MethodModel methodModel) {
        this.methodModel = methodModel;
    }

    /**
     * Returns the method associated with this Body.
     *
     * @return the method that owns this body.
     */
    public MethodModel getMethodModel() {
        return methodModel;
    }

    /**
     * Returns the method associated with this Body.
     *
     * @return the method that owns this body.
     */
    public MethodModel getMethodUnsafe() {
        return methodModel;
    }

    public int getLocalCount() {
        return localChain.size();
    }

    public Chain<Local> getLocals() {
        return localChain;
    }

    public Chain<Trap> getTraps() {
        return traps;
    }

    public Unit getThisUnit() {
        for (Unit u : getUnits()) {
            if (u instanceof IdentityStmt && ((IdentityStmt) u).getRightOp() instanceof ThisRef) {
                return u;
            }
        }

        throw new RuntimeException("couldn't find this-assignment!" + " in " + getMethodModel());
    }

    public Local getThisLocal() {
        return (Local) (((IdentityStmt) getThisUnit()).getLeftOp());
    }

    public Local getParameterLocal(final int i) {
        for (final Unit unit : getUnits()) {
            if (unit instanceof IdentityStmt is) {
                Value rightOp = is.getRightOp();
                if (rightOp instanceof ParameterRef pr) {
                    if (pr.getIndex() == i) {
                        return (Local) is.getLeftOp();
                    }
                }
            }
        }

        throw new RuntimeException("couldn't find parameterref" + i + " in " + getMethodModel());
    }

    /**
     * Get all the LHS of the identity statements assigning from parameter references.
     *
     * @return a list of size as per <code>getMethod().getParameterCount()</code> with all elements ordered as per the
     * parameter index.
     * @throws RuntimeException if a parameterref is missing
     */
    public List<Local> getParameterLocals() {
        final int parameterCount = getMethodModel().getParameterCount();
        Local[] res = new Local[parameterCount];
        int numberFound = 0;

        for (final Unit unit : getUnits()) {
            if (unit instanceof IdentityStmt identityStmt) {
                Value rightOp = identityStmt.getRightOp();

                if (rightOp instanceof ParameterRef) {
                    int index = ((ParameterRef) rightOp).getIndex();

                    if (res[index] != null) {
                        throw new IllegalStateException("duplicate parameterref" + index + " in " + getMethodModel());
                    }

                    res[index] = (Local) identityStmt.getLeftOp();
                    numberFound++;

                    if (numberFound >= parameterCount) {
                        break;
                    }
                }
            }
        }

        if (numberFound != parameterCount) {
            for (int i = 0; i < parameterCount; i++) {
                if (res[i] == null) {
                    throw new RuntimeException("couldn't find parameterref" + i + " in " + getMethodModel());
                }
            }
            throw new RuntimeException("couldn't find parameterref? in " + getMethodModel());
        }

        return Arrays.asList(res);
    }

    /**
     * Returns the list of parameter references used in this body. The list is as long as the number of parameters declared in
     * the associated method's signature. The list may have <code>null</code> entries for parameters not referenced in the
     * body. The returned list is of fixed size.
     *
     * @return
     */
    public List<Value> getParameterRefs() {
        final int numParams = getMethodModel().getParameterCount();
        final Value[] parameterRefs = new Value[numParams];
        int numFound = 0;

        for (final Unit unit : getUnits()) {
            if (unit instanceof IdentityStmt) {
                final Value rightOp = ((IdentityStmt) unit).getRightOp();

                if (rightOp instanceof ParameterRef parameterRef) {
                    int idx = parameterRef.getIndex();

                    if (parameterRefs[idx] != null) {
                        throw new RuntimeException("duplicate parameterref" + idx + " in " + getMethodModel());
                    }

                    parameterRefs[idx] = parameterRef;
                    numFound++;

                    if (numFound >= numParams) {
                        break;
                    }
                }
            }
        }

        return Arrays.asList(parameterRefs);
    }

    public UnitPatchingChain getUnits() {
        return units;
    }

    public List<UnitBox> getAllUnitBoxes() {
        ArrayList<UnitBox> unitBoxes = new ArrayList<>();

        for (final Unit unit : units) {
            unitBoxes.addAll(unit.getUnitBoxes());
        }

        for (final Trap trap : traps) {
            unitBoxes.addAll(trap.getUnitBoxes());
        }

        return unitBoxes;
    }

    public List<UnitBox> getUnitBoxes(boolean branchTarget) {
        ArrayList<UnitBox> unitBoxList = new ArrayList<>();

        for (final Unit unit : units) {
            if (unit.branches() == branchTarget) {
                unitBoxList.addAll(unit.getUnitBoxes());
            }
        }

        for (final Trap trap : traps) {
            unitBoxList.addAll(trap.getUnitBoxes());
        }

        return unitBoxList;
    }

    public List<ValueBox> getUseBoxes() {
        final var useBoxes = new ArrayList<ValueBox>();

        for (final Unit item : units) {
            useBoxes.addAll(item.getUseBoxes());
        }

        return useBoxes;
    }

    public List<ValueBox> getDefBoxes() {
        final var defBoxes = new ArrayList<ValueBox>();

        for (final Unit unit : units) {
            defBoxes.addAll(unit.getDefBoxes());
        }

        return defBoxes;
    }

    public List<ValueBox> getUseAndDefBoxes() {
        ArrayList<ValueBox> useAndDefBoxes = new ArrayList<>();

        for (final Unit item : units) {
            useAndDefBoxes.addAll(item.getUseBoxes());
            useAndDefBoxes.addAll(item.getDefBoxes());
        }

        return useAndDefBoxes;
    }

    public long getModificationCount() {
        return localChain.getModificationCount() + units.getModificationCount() + traps.getModificationCount();
    }
}
