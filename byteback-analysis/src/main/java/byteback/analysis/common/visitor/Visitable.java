package byteback.analysis.common.visitor;

public interface Visitable<T extends Visitor<?, ?>> {

    void apply(final T visitor);

}
