package byteback.analysis.common.visitor;

public interface Visitor<T extends Visitable<?>, R> {

    default void defaultCase(T value) {
    }

    R visit(final T value);

}
