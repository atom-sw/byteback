package byteback.analysis.body.common.syntax.graph;

import byteback.analysis.body.common.syntax.Body;
import byteback.analysis.body.common.syntax.stmt.Unit;
import byteback.analysis.common.syntax.Chain;

import java.util.Iterator;
import java.util.List;

public class Block implements Iterable<Unit> {
    private Unit head, tail;

    private Body body;

    private List<Block> predecessors, successors;

    private int length = 0, bodyIndex = 0;

    public Block(final Unit head, final Unit tail, final Body body, final int bodyIndex, final int length,
                 final BlockGraph blockGraph) {
        this.head = head;
        this.tail = tail;
        this.body = body;
        this.bodyIndex = bodyIndex;
        this.length = length;
    }

    public Body getBody() {
        return body;
    }

    @Override
    public Iterator<Unit> iterator() {
        return body == null ? null : body.getUnits().iterator(head, tail);
    }

    public void insertBefore(final Unit toInsert, final Unit point) {
        if (point == head) {
            head = toInsert;
        }

        body.getUnits().insertBefore(toInsert, point);
    }

    public void insertAfter(final Unit toInsert, final Unit point) {
        if (point == tail) {
            tail = toInsert;
        }

        body.getUnits().insertAfter(toInsert, point);
    }

    public boolean remove(final Unit item) {
        Chain<Unit> units = this.body.getUnits();

        if (item == head) {
            head = units.getSuccOf(item);
        } else if (item == tail) {
            tail = units.getPredOf(item);
        }

        return units.remove(item);
    }

    /**
     * Returns the Unit occurring immediately after some other Unit in the block.
     *
     * @param unit
     *          The Unit from which we wish to get it's successor.
     * @return The successor or null if <code>aItem</code> is the tail for this Block.
     *
     */
    public Unit getSuccOf(final Unit unit) {
        return unit == tail ? null : body.getUnits().getSuccOf(unit);
    }

    /**
     * Returns the Unit occurring immediately before some other Unit in the block.
     *
     * @param unit
     *          The Unit from which we wish to get it's predecessor.
     * @return The predecessor or null if <code>aItem</code> is the head for this Block.
     */
    public Unit getPredOf(final Unit unit) {
        return unit == head ? null : body.getUnits().getPredOf(unit);
    }

    /**
     * Set the index of this Block in the list of Blocks that partition its enclosing Body instance.
     *
     * @param index
     *          The index of this Block in the list of Blocks that partition it's enclosing Body instance.
     **/
    public void setBodyIndex(int index) {
        this.bodyIndex = index;
    }

    /**
     * Returns the index of this Block in the list of Blocks that partition it's enclosing Body instance.
     *
     * @return The index of the block in it's enclosing Body instance.
     */
    public int getBodyIndex() {
        return bodyIndex;
    }

    /**
     * Returns the first unit in this block.
     *
     * @return The first unit in this block.
     */
    public Unit getHead() {
        return head;
    }

    /**
     * Returns the last unit in this block.
     *
     * @return The last unit in this block.
     */
    public Unit getTail() {
        return tail;
    }

    /**
     * Sets the list of Blocks that are predecessors of this block in it's enclosing BlockGraph instance.
     *
     * @param predecessors
     *          The a List of Blocks that precede this block.
     *
     * @see BlockGraph
     */
    public void setPredecessors(List<Block> predecessors) {
        this.predecessors = predecessors;
    }

    /**
     * Returns the List of Block that are predecessors to this block,
     *
     * @return A list of predecessor blocks.
     * @see BlockGraph
     */
    public List<Block> getPredecessors() {
        return predecessors;
    }

    /**
     * Sets the list of Blocks that are successors of this block in it's enclosing BlockGraph instance.
     *
     * @param succs
     *          The a List of Blocks that succede this block.
     *
     * @see BlockGraph
     */
    public void setSuccs(List<Block> succs) {
        successors = succs;
    }

    /**
     * Returns the List of Blocks that are successors to this block,
     *
     * @return A list of successorblocks.
     * @see BlockGraph
     */
    public List<Block> getSuccs() {
        return successors;
    }

    public String toShortString() {
        return "Block #" + bodyIndex;
    }

    @Override
    public String toString() {
        StringBuilder strBuf = new StringBuilder();
        strBuf.append("Block ").append(bodyIndex).append(':').append(System.lineSeparator());

        // print out predecessors and successors.
        strBuf.append("[preds: ");
        if (predecessors != null) {
            for (Block b : predecessors) {
                strBuf.append(b.getBodyIndex()).append(' ');
            }
        }
        strBuf.append("] [succs: ");
        if (successors != null) {
            for (Block b : successors) {
                strBuf.append(b.getBodyIndex()).append(' ');
            }
        }
        strBuf.append(']').append(System.lineSeparator());

        // print out Units in the Block
        Iterator<Unit> basicBlockIt = body.getUnits().iterator(head, tail);

        if (basicBlockIt.hasNext()) {
            Unit someUnit = basicBlockIt.next();
            strBuf.append(someUnit.toString()).append(';').append(System.lineSeparator());

            while (basicBlockIt.hasNext()) {
                someUnit = basicBlockIt.next();
                if (someUnit == tail) {
                    break;
                }
                strBuf.append(someUnit.toString()).append(';').append(System.lineSeparator());
            }

            if (tail == null) {
                strBuf.append("error: null tail found; block length: ").append(length).append(System.lineSeparator());
            } else if (tail != head) {
                strBuf.append(tail).append(';').append(System.lineSeparator());
            }
        }

        return strBuf.toString();
    }
}
