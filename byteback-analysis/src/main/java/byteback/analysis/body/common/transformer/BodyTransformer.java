package byteback.analysis.body.common.transformer;

import soot.Body;

import java.util.Map;

public abstract class BodyTransformer extends soot.BodyTransformer {

    @Override
    public final void internalTransform(final Body body, final String phaseName, final Map<String, String> options) {
        transformBody(body);
    }

    public abstract void transformBody(final Body body);

}
