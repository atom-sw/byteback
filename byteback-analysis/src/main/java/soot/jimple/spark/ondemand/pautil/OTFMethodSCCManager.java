package soot.jimple.spark.ondemand.pautil;

import soot.ClassModel;
import soot.Scene;
import soot.SootMethod;
import soot.jimple.spark.ondemand.genericutil.DisjointSets;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class OTFMethodSCCManager {

    private final DisjointSets disj;
    private final Map<SootMethod, Integer> numbers = new HashMap<>();

    public OTFMethodSCCManager() {
        int num = 0;
        for (ClassModel c : Scene.v().getClasses()) {
            for (SootMethod m : c.getMethods()) {
                numbers.put(m, num++);
            }
        }
        disj = new DisjointSets(num + 1);
    }

    public boolean inSameSCC(SootMethod m1, SootMethod m2) {
        return disj.find(numbers.get(m1)) == disj.find(numbers.get(m2));
    }

    public void makeSameSCC(Set<SootMethod> methods) {
        SootMethod prevMethod = null;
        for (SootMethod method : methods) {
            if (prevMethod != null) {
                int prevMethodRep = disj.find(numbers.get(prevMethod));
                int methodRep = disj.find(numbers.get(method));
                if (prevMethodRep != methodRep) {
                    disj.union(prevMethodRep, methodRep);
                }
            }
            prevMethod = method;
        }
    }
}
