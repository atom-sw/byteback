package byteback.syntax.scene.type.declaration.member.method.tag;

import soot.Local;
import soot.jimple.IdentityRef;
import soot.jimple.IdentityStmt;
import soot.tagkit.AttributeValueException;
import soot.tagkit.Tag;

import java.util.ArrayList;
import java.util.List;

public class InputRefsTag implements Tag {

    public static final String NAME = "InputRefsTag";

    private final List<IdentityStmt> identityStmts;

    public InputRefsTag(final List<IdentityStmt> identityStmts) {
        this.identityStmts = identityStmts;
    }

    public List<IdentityStmt> getIdentityStmts() {
        return identityStmts;
    }

    public List<Local> getInputLocals() {
        final var identityLocals = new ArrayList<Local>();

        for (final IdentityStmt identityStmt : getIdentityStmts()) {
            identityLocals.add((Local) identityStmt.getLeftOp());
        }

        return identityLocals;
    }

    public List<IdentityRef> getInputRefs() {
        final var identityRefs = new ArrayList<IdentityRef>();

        for (final IdentityStmt identityStmt : getIdentityStmts()) {
            identityRefs.add((IdentityRef) identityStmt.getRightOp());
        }

        return identityRefs;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public byte[] getValue() throws AttributeValueException {
        return new byte[0];
    }

}
