package byteback.analysis.body.common.syntax.stmt;

import byteback.analysis.model.syntax.ClassModel;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Partial implementation of trap (exception catcher), used within Body classes.
 */
public class Trap implements Serializable {

    /**
     * The exception being caught.
     */
    protected transient ClassModel exception;

    /**
     * The first unit being trapped.
     */
    protected UnitBox beginUnitBox;

    /**
     * The unit just before the last unit being trapped.
     */
    protected UnitBox endUnitBox;

    /**
     * The unit to which execution flows after the caught exception is triggered.
     */
    protected UnitBox handlerUnitBox;

    /**
     * The list of UnitBoxes referred to in this Trap (begin, end, and handler).
     */
    protected List<UnitBox> unitBoxes;

    /**
     * Creates an AbstractTrap with the given exception, handler, begin, and end units.
     */
    protected Trap(ClassModel exception, UnitBox beginUnitBox, UnitBox endUnitBox, UnitBox handlerUnitBox) {
        this.exception = exception;
        this.beginUnitBox = beginUnitBox;
        this.endUnitBox = endUnitBox;
        this.handlerUnitBox = handlerUnitBox;
        this.unitBoxes = Collections.unmodifiableList(Arrays.asList(beginUnitBox, endUnitBox, handlerUnitBox));
    }

    public Unit getBeginUnit() {
        return beginUnitBox.getUnit();
    }

    public Unit getEndUnit() {
        return endUnitBox.getUnit();
    }

    public Unit getHandlerUnit() {
        return handlerUnitBox.getUnit();
    }

    public UnitBox getHandlerUnitBox() {
        return handlerUnitBox;
    }

    public UnitBox getBeginUnitBox() {
        return beginUnitBox;
    }

    public UnitBox getEndUnitBox() {
        return endUnitBox;
    }

    public List<UnitBox> getUnitBoxes() {
        return unitBoxes;
    }

    public void clearUnitBoxes() {
        for (final UnitBox box : getUnitBoxes()) {
            box.setUnit(null);
        }
    }

    public ClassModel getException() {
        return exception;
    }

    public void setBeginUnit(final Unit beginUnit) {
        beginUnitBox.setUnit(beginUnit);
    }

    public void setEndUnit(final Unit endUnit) {
        endUnitBox.setUnit(endUnit);
    }

    public void setHandlerUnit(Unit handlerUnit) {
        handlerUnitBox.setUnit(handlerUnit);
    }

    public void setException(ClassModel exception) {
        this.exception = exception;
    }

    @Override
    public Object clone() {
        throw new RuntimeException();
    }
}
