package byteback.analysis.body.common.transformer;

import soot.Body;

import java.util.Map;

/**
 * Base class for the transformer of a body of a method.
 * @author paganma
 */
public abstract class BodyTransformer extends soot.BodyTransformer {

    @Override
    public void internalTransform(final Body body, final String phaseName, final Map<String, String> options) {
        transformBody(body);
    }

    public abstract void transformBody(final Body body);

}
