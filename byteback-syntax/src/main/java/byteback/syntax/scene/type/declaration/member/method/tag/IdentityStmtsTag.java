package byteback.syntax.scene.type.declaration.member.method.tag;

import soot.jimple.IdentityStmt;
import soot.tagkit.AttributeValueException;
import soot.tagkit.Tag;

import java.util.List;

public class IdentityStmtsTag implements Tag {

    public static final String NAME = "ParametersLocalsTag";

    final List<IdentityStmt> identityStmts;

    public IdentityStmtsTag(final List<IdentityStmt> identityStmts) {
        this.identityStmts = identityStmts;
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
