package byteback.encoder.boogie.type;

import byteback.encoder.common.type.ClassEncoder;
import soot.Scene;
import soot.SootClass;

import java.io.PrintWriter;

public class ClassToBplEncoder extends ClassEncoder {

    public ClassToBplEncoder(final PrintWriter writer) {
        super(writer);
    }

    @Override
    public void transformClass(final Scene scene, final SootClass sootClass) {

    }

}
