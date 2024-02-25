package byteback.encoder.boogie;

import soot.Body;
import soot.BodyTransformer;

import java.util.Map;

public class BplEncoder extends BodyTransformer {

    @Override
    protected void internalTransform(Body b, String phaseName, Map<String, String> options) {
        System.out.println("=======");
        System.out.println(b.getMethod());
        System.out.println(b);
    }

}