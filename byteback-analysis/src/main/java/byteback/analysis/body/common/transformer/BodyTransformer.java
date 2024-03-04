package byteback.analysis.body.common.transformer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import soot.Body;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Map;

public abstract class BodyTransformer extends soot.BodyTransformer {

    final Logger logger = LoggerFactory.getLogger(BodyTransformer.class);

    public void initialize(final String phaseName, final Map<String, String> options) {}

    @Override
    public final void internalTransform(final Body body, final String phaseName, final Map<String, String> options) {
        initialize(phaseName, options);
        transformBody(body);
    }

    public abstract void transformBody(final Body body);

}
