package byteback.analysis.body.common;

import byteback.analysis.body.common.syntax.*;
import byteback.analysis.body.jimple.syntax.stmt.IdentityStmt;
import byteback.analysis.common.syntax.Chain;
import byteback.analysis.common.syntax.HashChain;
import byteback.analysis.model.syntax.MethodModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.*;

public abstract class Body implements Serializable {
    private static final Logger logger = LoggerFactory.getLogger(Body.class);

    /**
     * The method associated with this Body.
     */
    protected transient MethodModel method = null;

    /**
     * The chain of locals for this Body.
     */
    protected Chain<Local> localChain = new HashChain<>();

    /**
     * The chain of traps for this Body.
     */
    protected Chain<Trap> trapChain = new HashChain<>();

    /**
     * The chain of units for this Body.
     */
    protected UnitPatchingChain units = new UnitPatchingChain(new HashChain<>());

    abstract public Object clone(boolean noLocalsClone);

    /**
     * Creates a Body associated to the given method. Used by subclasses during initialization. Creation of a Body is triggered
     * by e.g. Jimple.v().newBody(options).
     *
     * @param m
     */
    protected Body(MethodModel m) {
        this.method = m;
    }

    /**
     * Creates an extremely empty Body. The Body is not associated to any method.
     */
    protected Body() {
    }

    /**
     * Returns the method associated with this Body.
     *
     * @return the method that owns this body.
     */
    public MethodModel getMethodModel() {
        if (method == null) {
            throw new RuntimeException("no method associated w/ body");
        }
        return method;
    }

    /**
     * Returns the method associated with this Body.
     *
     * @return the method that owns this body.
     */
    public MethodModel getMethodUnsafe() {
        return method;
    }

    /**
     * Sets the method associated with this Body.
     *
     * @param method the method that owns this body.
     */
    public void setMethod(MethodModel method) {
        this.method = method;
    }

    /**
     * Returns the number of locals declared in this body.
     *
     * @return
     */
    public int getLocalCount() {
        return localChain.size();
    }


    public Chain<Local> getLocals() {
        return localChain;
    }

    /**
     * Returns a backed view of the traps found in this Body.
     *
     * @return
     */
    public Chain<Trap> getTraps() {
        return trapChain;
    }

    /**
     * Return unit containing the \@this-assignment
     *
     * @return
     */
    public Unit getThisUnit() {
        for (Unit u : getUnits()) {
            if (u instanceof IdentityStmt && ((IdentityStmt) u).getRightOp() instanceof ThisRef) {
                return u;
            }
        }

        throw new RuntimeException("couldn't find this-assignment!" + " in " + getMethodModel());
    }

    /**
     * Return LHS of the first identity stmt assigning from \@this.
     *
     * @return
     */
    public Local getThisLocal() {
        return (Local) (((IdentityStmt) getThisUnit()).getLeftOp());
    }

    /**
     * Return LHS of the first identity stmt assigning from \@parameter i.
     *
     * @param i
     * @return
     */
    public Local getParameterLocal(int i) {
        for (Unit s : getUnits()) {
            if (s instanceof IdentityStmt is) {
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
        final int numParams = getMethodModel().getParameterCount();
        Local[] res = new Local[numParams];
        int numFound = 0;
        for (byteback.analysis.body.jimple.syntax.Unit u : getUnits()) {
            if (u instanceof IdentityStmt is) {
                Value rightOp = is.getRightOp();
                if (rightOp instanceof ParameterRef) {
                    int idx = ((ParameterRef) rightOp).getIndex();
                    if (res[idx] != null) {
                        throw new RuntimeException("duplicate parameterref" + idx + " in " + getMethodModel());
                    }
                    res[idx] = (Local) is.getLeftOp();
                    numFound++;
                    if (numFound >= numParams) {
                        break;
                    }
                }
            }
        }
        if (numFound != numParams) {
            for (int i = 0; i < numParams; i++) {
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
        Value[] res = new Value[numParams];
        int numFound = 0;
        for (final Unit unit : getUnits()) {
            if (unit instanceof IdentityStmt) {
                Value rightOp = ((IdentityStmt) unit).getRightOp();
                if (rightOp instanceof ParameterRef pr) {
                    int idx = pr.getIndex();
                    if (res[idx] != null) {
                        throw new RuntimeException("duplicate parameterref" + idx + " in " + getMethodModel());
                    }
                    res[idx] = pr;
                    numFound++;
                    if (numFound >= numParams) {
                        break;
                    }
                }
            }
        }
        return Arrays.asList(res);
    }

    public UnitPatchingChain getUnits() {
        return units;
    }

    public List<UnitBox> getAllUnitBoxes() {
        ArrayList<UnitBox> unitBoxList = new ArrayList<>();

        for (Unit item : units) {
            unitBoxList.addAll(item.getUnitBoxes());
        }

        for (Trap item : trapChain) {
            unitBoxList.addAll(item.getUnitBoxes());
        }

        return unitBoxList;
    }

    public List<UnitBox> getUnitBoxes(boolean branchTarget) {
        ArrayList<UnitBox> unitBoxList = new ArrayList<>();

        for (Unit item : units) {
            if (item.branches() == branchTarget) {
                unitBoxList.addAll(item.getUnitBoxes());
            }
        }

        for (Trap item : trapChain) {
            unitBoxList.addAll(item.getUnitBoxes());
        }

        return unitBoxList;
    }

    /**
     * Returns the result of iterating through all Units in this body and querying them for ValueBoxes used. All of the
     * ValueBoxes found are then returned as a List.
     *
     * @return a list of all the ValueBoxes for the Values used this body's units.
     * @see Value
     * @see byteback.analysis.body.jimple.syntax.Unit#getUseBoxes
     * @see ValueBox
     * @see Value
     */
    public List<ValueBox> getUseBoxes() {
        ArrayList<ValueBox> useBoxList = new ArrayList<>();
        for (byteback.analysis.body.jimple.syntax.Unit item : units) {
            useBoxList.addAll(item.getUseBoxes());
        }
        return useBoxList;
    }

    /**
     * Returns the result of iterating through all Units in this body and querying them for ValueBoxes defined. All of the
     * ValueBoxes found are then returned as a List.
     *
     * @return a list of all the ValueBoxes for Values defined by this body's units.
     * @see Value
     * @see byteback.analysis.body.jimple.syntax.Unit#getDefBoxes
     * @see ValueBox
     * @see Value
     */
    public List<ValueBox> getDefBoxes() {
        ArrayList<ValueBox> defBoxList = new ArrayList<>();
        for (byteback.analysis.body.jimple.syntax.Unit item : units) {
            defBoxList.addAll(item.getDefBoxes());
        }
        return defBoxList;
    }

    /**
     * Returns a list of boxes corresponding to Values either used or defined in any unit of this Body.
     *
     * @return a list of ValueBoxes for held by the body's Units.
     * @see Value
     * @see byteback.analysis.body.jimple.syntax.Unit#getUseAndDefBoxes
     * @see ValueBox
     * @see Value
     */
    public List<ValueBox> getUseAndDefBoxes() {
        ArrayList<ValueBox> useAndDefBoxList = new ArrayList<>();

        for (final Unit item : units) {
            useAndDefBoxList.addAll(item.getUseBoxes());
            useAndDefBoxList.addAll(item.getDefBoxes());
        }

        return useAndDefBoxList;
    }

    public long getModificationCount() {
        return localChain.getModificationCount() + units.getModificationCount() + trapChain.getModificationCount();
    }
}
