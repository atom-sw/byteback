package byteback.encoder.boogie.type;

import byteback.encoder.common.type.ClassEncoder;
import soot.FastHierarchy;
import soot.Scene;
import soot.SootClass;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;

public class HierarchyToBplEncoder extends ClassEncoder {

    public HierarchyToBplEncoder(PrintWriter writer) {
        super(writer);
    }

    @Override
    public void transformClass(final Scene scene, final SootClass sootClass) {
    }

}
