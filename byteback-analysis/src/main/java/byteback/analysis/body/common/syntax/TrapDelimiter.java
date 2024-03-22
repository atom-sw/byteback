package byteback.analysis.body.common.syntax;


import soot.Trap;

public final class TrapDelimiter {

    public enum Type {
        STARTER, ENDER
    }

    private final Trap trap;

    private final Type type;

    public TrapDelimiter(final Trap trap, final Type type) {
        this.trap = trap;
        this.type = type;
    }

    public Trap getTrap() {
        return trap;
    }

    public Type getType() {
        return type;
    }

}