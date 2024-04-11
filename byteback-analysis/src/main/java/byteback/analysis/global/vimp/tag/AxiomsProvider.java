package byteback.analysis.global.vimp.tag;

import byteback.analysis.common.tag.TagProvider;
import byteback.common.function.Lazy;
import soot.SootClass;

import java.util.ArrayList;

public class AxiomsProvider extends TagProvider<SootClass, AxiomsTag> {

    private static final Lazy<AxiomsProvider> instance =
            Lazy.from(()  -> new AxiomsProvider(AxiomsTag.NAME));

    public static AxiomsProvider v() {
        return instance.get();
    }

    private AxiomsProvider(final String tagName) {
        super(tagName);
    }

    @Override
    public AxiomsTag compute(final SootClass sootClass) {
        return new AxiomsTag(new ArrayList<>());
    }

}