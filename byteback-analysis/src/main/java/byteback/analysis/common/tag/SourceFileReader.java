package byteback.analysis.common.tag;

import byteback.common.function.Lazy;
import soot.tagkit.Host;
import soot.tagkit.SourceFileTag;

public class SourceFileReader extends TagReader<Host, SourceFileTag> {

    private static final Lazy<SourceFileReader> instance = Lazy.from(()  -> new SourceFileReader("SourceFileTag"));

    public static SourceFileReader v() {
        return instance.get();
    }

    private SourceFileReader(final String tagName) {
        super(tagName);
    }

}
