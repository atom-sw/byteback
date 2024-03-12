package soot.jimple.toolkits.annotation;

import soot.*;
import soot.jimple.IdentityStmt;
import soot.tag.LineNumberTag;
import soot.util.Chain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class LineNumberAdder extends SceneTransformer {

    public LineNumberAdder(Singletons.Global g) {
    }

    public static LineNumberAdder v() {
        return G.v().soot_jimple_toolkits_annotation_LineNumberAdder();
    }

    @Override
    public void internalTransform(String phaseName, Map<String, String> opts) {
        // using a snapshot iterator because Application classes may change if LambdaMetaFactory translates
        // invokedynamic to new classes; no need to visit new classes
        for (Iterator<ClassModel> it = Scene.v().getApplicationClasses().snapshotIterator(); it.hasNext(); ) {
            ClassModel sc = it.next();
            // make map of first line to each method
            HashMap<Integer, SootMethod> lineToMeth = new HashMap<Integer, SootMethod>();
            for (SootMethod meth : new ArrayList<>(sc.getMethods())) {
                if (!meth.isConcrete()) {
                    continue;
                }
                Chain<Unit> units = meth.retrieveActiveBody().getUnits();
                Unit s = units.getFirst();
                while (s instanceof IdentityStmt) {
                    s = units.getSuccOf(s);
                }
                LineNumberTag tag = (LineNumberTag) s.getTag(LineNumberTag.NAME);
                if (tag != null) {
                    lineToMeth.put(tag.getLineNumber(), meth);
                }
            }
            for (SootMethod meth : sc.getMethods()) {
                if (!meth.isConcrete()) {
                    continue;
                }
                Chain<Unit> units = meth.retrieveActiveBody().getUnits();
                Unit s = units.getFirst();
                while (s instanceof IdentityStmt) {
                    s = units.getSuccOf(s);
                }
                LineNumberTag tag = (LineNumberTag) s.getTag(LineNumberTag.NAME);
                if (tag != null) {
                    int line_num = tag.getLineNumber() - 1;
                    // already taken
                    if (lineToMeth.containsKey(line_num)) {
                        meth.addTag(new LineNumberTag(line_num + 1));
                    } else {
                        // still available - so use it for this meth
                        meth.addTag(new LineNumberTag(line_num));
                    }
                }
            }
        }
    }
}
