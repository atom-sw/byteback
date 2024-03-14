package byteback.analysis.common.syntax;

public interface EquivTo {

    /**
     * Returns true if this object is equivalent to o.
     */
    boolean equivTo(Object o);

    /**
     * Returns a (not necessarily fixed) hash code for this object. This hash code coincides with equivTo; it is undefined in
     * the presence of mutable objects.
     */
    int equivHashCode();
}