package byteback.encoder.boogie;

import soot.*;

import java.util.Map;

public class BplEncoder extends BodyTransformer {

    @Override
    protected void internalTransform(Body b, String phaseName, Map<String, String> options) {
        System.out.println(b);
    }

}